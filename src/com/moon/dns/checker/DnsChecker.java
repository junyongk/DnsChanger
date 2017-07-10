package com.moon.dns.checker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

public class DnsChecker {
	
	
	public static Enum getIpAssignment(WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	        Object ipConfiguration;
			try {
				ipConfiguration = wifiConf.getClass().getMethod("getIpConfiguration").invoke(wifiConf);
				return (Enum)getField(ipConfiguration, "ipAssignment");
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	    } else{
	    	return (Enum)getField(wifiConf, "ipAssignment");
	    }
		return null;
		
	}

	public static ArrayList<String> getDns(Context cont)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException,
			UnknownHostException, InstantiationException, NoSuchMethodException, InvocationTargetException {

		ArrayList<String> dnss = new ArrayList<String>();
		if (Build.VERSION.SDK_INT < 17) {

			dnss.add(android.provider.Settings.System.getString(
					cont.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_DNS1));
			dnss.add(android.provider.Settings.System.getString(
					cont.getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_DNS2));

		}else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
			
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
					
			ArrayList<InetAddress> mDnses;

			Object linkProperties = getField(wifiConf, "linkProperties");
			if (linkProperties == null)
				return null;
			mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties,
					"mDnses");

			for (InetAddress d : mDnses) {
				dnss.add(d.getHostAddress());
			}

			
		}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			
			WifiManager wifiManager = (WifiManager)cont.getSystemService(Context.WIFI_SERVICE);
			WifiConfiguration wifiConf = null;
			WifiInfo connectionInfo = wifiManager.getConnectionInfo();
	        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();        
	        
	        if(configuredNetworks==null) return null;
	        for (WifiConfiguration conf : configuredNetworks){
	            if (conf.networkId == connectionInfo.getNetworkId()){
	                wifiConf = conf;
	                break;              
	            }
	        }
					
			ArrayList<InetAddress> mDnses;
			
			Method mgetIpConfiguration = getMethod(wifiConf,"getIpConfiguration");
			
			if(mgetIpConfiguration==null) return null;
			
			Object IpConf = mgetIpConfiguration.invoke(wifiConf);
			
			if(IpConf==null) return null;
			
			Method mgetStaticIpConfiguration = getMethod(wifiConf,"getStaticIpConfiguration");
			
			if(mgetStaticIpConfiguration==null) return null;
			
			Object staticIpConf = mgetStaticIpConfiguration.invoke(wifiConf);
			
			
			Enum ipa = getIpAssignment(wifiConf);
			if(ipa.name().equals("DHCP")){
				
				ConnectivityManager cm = (ConnectivityManager) cont
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				Network[] ns = cm.getAllNetworks();

				for (Network n : ns) {
					if (n != null) {

						LinkProperties prop = cm.getLinkProperties(n);

						List<InetAddress> mDnsess =  prop.getDnsServers();
						for(InetAddress addr : mDnsess){
							dnss.add(addr.getHostAddress());
						}
						
					
					}
				}
			}else{
				mDnses = (ArrayList<InetAddress>) getDeclaredField(staticIpConf, "dnsServers");
				
				if(mDnses==null )return null;
				
				for (InetAddress d : mDnses) {
					dnss.add(d.getHostAddress());
				}		
			}
			
			
			
			
		}
		
		return dnss;
	}

	public static Method getMethod(Object obj, String name, Class<?>... prams) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException, NoSuchMethodException {
		return obj.getClass().getMethod(name,prams);
      
    }
	
	public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        
        Object out = f.get(obj);
        return out;
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
