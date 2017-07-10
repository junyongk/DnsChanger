package com.moon.dns.changer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.moon.dns.checker.DnsChecker;

public class DnsChanger {
	
	
	private static InetAddress getInetAddress(int ip){
		final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.putInt(ip);
		try {
		final InetAddress inetAddress = InetAddress.getByAddress(null, byteBuffer.array());
		
		return inetAddress;
		} catch (UnknownHostException e) {
		    return null;
		}
		
	}

	public String getIpAddress(WifiManager paramWifiManager) {
		int i = paramWifiManager.getConnectionInfo().getIpAddress();
		return String.format(
				"%d.%d.%d.%d",
				new Object[] { Integer.valueOf(i & 0xFF),
						Integer.valueOf(i >> 8 & 0xFF),
						Integer.valueOf(i >> 16 & 0xFF),
						Integer.valueOf(i >> 24 & 0xFF) });
	}
	
	public String getGWIpAddress(WifiManager paramWifiManager) {
		
		int paramInt = paramWifiManager.getDhcpInfo().gateway;
		return (paramInt & 0xFF) + "." + (paramInt >> 8 & 0xFF) + "." + (paramInt >> 16 & 0xFF) + "." + (paramInt >> 24 & 0xFF);
		  
		
	}

	public static void setIpAssignment(String assign, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	        Object ipConfiguration;
			try {
				ipConfiguration = wifiConf.getClass().getMethod("getIpConfiguration").invoke(wifiConf);
				setEnumField(ipConfiguration, assign, "ipAssignment");
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	    } else{
	    	setEnumField(wifiConf, assign, "ipAssignment");
	    }
		
	}
	
	
	public static void reset(Context cont) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, UnknownHostException, InstantiationException{
		String defdns1=null;
		String defdns2=null;
		
		SharedPreferences pref = cont.getSharedPreferences("dns",Activity.MODE_PRIVATE);
		
		
		defdns1 = pref.getString("dns1", null);
		defdns2 = pref.getString("dns2", null);
		
		if(defdns1==null && defdns2==null){
			
			return;
		}
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){ //JELL BEAN MR1 4.2 미만
			
			android.provider.Settings.System.putString(cont.getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "0");    
			
			
		}else {
			
			WifiManager wifiManager = (WifiManager)cont.getSystemService(Context.WIFI_SERVICE);
			WifiConfiguration wifiConf = null;
			WifiInfo connectionInfo = wifiManager.getConnectionInfo();
	        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();        
	        for (WifiConfiguration conf : configuredNetworks){
	            if (conf.networkId == connectionInfo.getNetworkId()){
	                wifiConf = conf;
	                break;              
	            }
	        }
			
	        setIpAssignment("DHCP",wifiConf);
			
	        ArrayList<InetAddress> mDnses;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //LOLLIPOP 5.0 이상
				
				Method mgetIpConfiguration = getMethod(wifiConf,"getIpConfiguration");
				
				if(mgetIpConfiguration==null) return;
				
				Object IpConf = mgetIpConfiguration.invoke(wifiConf);
				
				if(IpConf==null) return;
				
				
				Object staticIpConfiguration  = getNewInstance("android.net.StaticIpConfiguration");
				if(staticIpConfiguration ==null) return;
				mDnses = (ArrayList<InetAddress>) getDeclaredField(staticIpConfiguration, "dnsServers");
				setField(staticIpConfiguration,getNewInstance("android.net.LinkAddress", new Class[] { InetAddress.class, Integer.TYPE }, new Object[] { getInetAddress(wifiManager.getDhcpInfo().ipAddress), 	Integer.bitCount(wifiManager.getDhcpInfo().netmask) }),"ipAddress");
				setField(staticIpConfiguration,getInetAddress(wifiManager.getDhcpInfo().gateway),"gateway");
				
				if(defdns1!=null)mDnses.add(InetAddress.getByName(defdns1));
				if(defdns2!=null)mDnses.add(InetAddress.getByName(defdns2));
				Log.e("TEST", mDnses.get(0).getHostAddress());
				
				Method msetStaticIpConfiguration = getMethod(wifiConf,"setStaticIpConfiguration",new Class[]{Class.forName("android.net.StaticIpConfiguration")});
				
				msetStaticIpConfiguration.invoke(wifiConf, staticIpConfiguration);
				
			}else {
				Object linkProperties = getField(wifiConf, "linkProperties");
				if(linkProperties==null) return;
				mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
				
				mDnses.clear();
				if(defdns1!=null)mDnses.add(InetAddress.getByName(defdns1));
				if(defdns2!=null)mDnses.add(InetAddress.getByName(defdns2));
				
				Object LinkAddress=getNewInstance("android.net.LinkAddress", new Class[] { InetAddress.class, Integer.TYPE }, new Object[] { getInetAddress(wifiManager.getDhcpInfo().ipAddress), 	Integer.bitCount(wifiManager.getDhcpInfo().netmask) });
				
				ArrayList<Object> listlinkaddr =(ArrayList<Object>) getDeclaredField(linkProperties, "mLinkAddresses");
				listlinkaddr.clear();
				listlinkaddr.add(LinkAddress);
				
				Object Routeinfo=getNewInstance("android.net.RouteInfo",new Class[] { InetAddress.class }, new Object[] { getInetAddress(wifiManager.getDhcpInfo().gateway)});
				
				ArrayList<Object> routes =(ArrayList<Object>) getDeclaredField(linkProperties, "mRoutes");
				routes.clear();
				routes.add(Routeinfo);
				Log.e("TEST", mDnses.get(0).getHostAddress());
			}
			

			wifiManager.updateNetwork(wifiConf);
			wifiManager.saveConfiguration();
			wifiManager.setWifiEnabled(false);
			wifiManager.setWifiEnabled(true);
		
			
		}
		
	}
	public static void setDns(Context cont,String dns1,String dns2) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, UnknownHostException, InvocationTargetException, NoSuchMethodException, InstantiationException, ClassNotFoundException{
		String defdns1=null;
		String defdns2=null;
		
		SharedPreferences pref = cont.getSharedPreferences("dns",Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		defdns1 = pref.getString("dns1", null);
		defdns2 = pref.getString("dns2", null);
		
		if(defdns1==null && defdns2==null){
			
	
			List<String> dnss = DnsChecker.getDns(cont);
			
			
			
			if(dnss!=null){
				if(dnss.size()>=1){
					editor.putString("dns1", dnss.get(0));
					editor.commit();
					
				}
				if(dnss.size()>=2){
					editor.putString("dns2", dnss.get(1));
					editor.commit();
					
				}
			}
		}
		
		WifiManager wifiManager = (WifiManager)cont.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wifiConf = null;
		WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();        
        for (WifiConfiguration conf : configuredNetworks){
            if (conf.networkId == connectionInfo.getNetworkId()){
                wifiConf = conf;
                break;              
            }
        }
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){ //JELL BEAN MR1 4.2 미만
			
			
			android.provider.Settings.System.putString(cont.getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP, "1");    
			android.provider.Settings.System.putString(cont.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_IP,getInetAddress(wifiManager.getDhcpInfo().ipAddress).getHostAddress());
			android.provider.Settings.System.putString(cont.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_GATEWAY,getInetAddress(wifiManager.getDhcpInfo().gateway).getHostAddress());
			if(dns1!=null)android.provider.Settings.System.putString(cont.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_DNS1, dns1);
			if(dns2!=null)android.provider.Settings.System.putString(cont.getContentResolver(), android.provider.Settings.System.WIFI_STATIC_DNS1, dns2);
			
		}else {
			
			
			
	        setIpAssignment("STATIC",wifiConf);
		
			
			ArrayList<InetAddress> mDnses;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //LOLLIPOP 5.0 이상
				
				Method mgetIpConfiguration = getMethod(wifiConf,"getIpConfiguration");
				
				if(mgetIpConfiguration==null) return;
				
				Object IpConf = mgetIpConfiguration.invoke(wifiConf);
				
				if(IpConf==null) return;
				
				
				Object staticIpConfiguration  = getNewInstance("android.net.StaticIpConfiguration");
				if(staticIpConfiguration ==null) return;
				mDnses = (ArrayList<InetAddress>) getDeclaredField(staticIpConfiguration, "dnsServers");
				
			
				setField(staticIpConfiguration,getNewInstance("android.net.LinkAddress", new Class[] { InetAddress.class, Integer.TYPE }, new Object[] { getInetAddress(wifiManager.getDhcpInfo().ipAddress), 	Integer.bitCount(wifiManager.getDhcpInfo().netmask) }),"ipAddress");
				setField(staticIpConfiguration,getInetAddress(wifiManager.getDhcpInfo().gateway),"gateway");
				if(dns1!=null)mDnses.add(InetAddress.getByName(dns1));
				if(dns2!=null)mDnses.add(InetAddress.getByName(dns2));
				Log.e("TEST", mDnses.get(0).getHostAddress());
				
				Method msetStaticIpConfiguration = getMethod(wifiConf,"setStaticIpConfiguration",new Class[]{Class.forName("android.net.StaticIpConfiguration")});
				
				msetStaticIpConfiguration.invoke(wifiConf, staticIpConfiguration);
				
			}else {
				Object linkProperties = getField(wifiConf, "linkProperties");
				if(linkProperties==null) return;
				
				mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
				mDnses.clear();
				
				if(dns1!=null)mDnses.add(InetAddress.getByName(dns1));
				if(dns2!=null)mDnses.add(InetAddress.getByName(dns2));
				
				
				Object LinkAddress=getNewInstance("android.net.LinkAddress", new Class[] { InetAddress.class, Integer.TYPE }, new Object[] { getInetAddress(wifiManager.getDhcpInfo().ipAddress), 	Integer.bitCount(wifiManager.getDhcpInfo().netmask) });
				
				ArrayList<Object> listlinkaddr =(ArrayList<Object>) getDeclaredField(linkProperties, "mLinkAddresses");
				listlinkaddr.clear();
				listlinkaddr.add(LinkAddress);
				
				
				Object Routeinfo=getNewInstance("android.net.RouteInfo",new Class[] { InetAddress.class }, new Object[] { getInetAddress(wifiManager.getDhcpInfo().gateway)});
				
				ArrayList<Object> routes =(ArrayList<Object>) getDeclaredField(linkProperties, "mRoutes");
				routes.clear();
				routes.add(Routeinfo);
			
				Log.e("TEST", mDnses.get(0).getHostAddress());
			}
		
			
			wifiManager.updateNetwork(wifiConf);
			wifiManager.saveConfiguration();
			
			wifiManager.setWifiEnabled(false);
			wifiManager.setWifiEnabled(true);
		
			
			
			
			
		}
	}
	
	public static Method getMethod(Object obj, String name, Class<?>... prams) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException, NoSuchMethodException {
		return obj.getClass().getMethod(name,prams);
      
    }
	
	public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        
        Object out = f.get(obj);
        return out;
    }
	
	public static void setField(Object obj,Object value, String name) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        
        f.set(obj, value);
       
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }
   
    
	public static void setEnumField(Object obj, String value, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
	}
	
	public Object getEnumField(Object obj,  String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		return f.get(obj);
		
		
	}
	
	public static Object getNewInstance(String paramString, Class<?>[] paramArrayOfClass,
			Object[] paramArrayOfObject) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException {
		return Class.forName(paramString).getConstructor(paramArrayOfClass)
				.newInstance(paramArrayOfObject);
	}
	
	public static Object getNewInstance(String name) throws InstantiationException, IllegalAccessException{
		Class c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 Object o = c.newInstance();
		
		 return o;
	}
    
 

}
