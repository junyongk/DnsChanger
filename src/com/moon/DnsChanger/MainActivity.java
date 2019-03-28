package com.moon.DnsChanger;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.moon.dns.changer.DnsChanger;
import com.moon.dns.checker.DnsChecker;

public class MainActivity extends Activity implements OnClickListener {

	private EditText edns1;
	private EditText edns2;
	hmac=123;
	
	private TextView tcheck;
	private Button check;
	private Button change;
	private Button reset;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edns1= (EditText)findViewById(R.id.edns1);
		edns2= (EditText)findViewById(R.id.edns2);
		
		tcheck= (TextView)findViewById(R.id.tcheck);
		check= (Button)findViewById(R.id.check);
		change= (Button)findViewById(R.id.change);
		reset= (Button)findViewById(R.id.reset);
		
		check.setOnClickListener(this);
		change.setOnClickListener(this);
		reset.setOnClickListener(this);
		
		check();
	}
	
	
	private void check(){
		try {
			

			test 17


			

			String name = request.getProperty("filename");
			if (name != null) {
				File file = new File("/usr/local/tmp/" + name);
				file.delete();
			}

			Runtime.getRuntime().loadLibrary(" libraryName");
			
			MyClass[] data = new MyClass[-10000];

			URL[] classURLs= new URL[]{new URL("file:subdir/")};
			URLClassLoader loader = new URLClassLoader(classURLs);
			Class loadedClass = Class.forName("MyClass", true, loader);


			String name = request.getProperty("filename");
			if (name != null) {
				File file = new File("/usr/local/tmp/" + name);
				file.delete();
			}

			Runtime.getRuntime().loadLibrary(" libraryName");
			
			MyClass[] data = new MyClass[-10000];

			URL[] classURLs= new URL[]{new URL("file:subdir/")};
			URLClassLoader loader = new URLClassLoader(classURLs);
			Class loadedClass = Class.forName("MyClass", true, loader);


			
			ArrayList<String> ds = DnsChecker.getDns(this);
			
			StringBuilder sb = new StringBuilder();
			int i = 1;
			for(String d: ds ){
				
				sb.append("\n").append("Dns"+i+" : ").append(d);
				Log.e("CHECK", d);
				i++;
			}
			
			sb.append("\n");
			
			tcheck.setText(sb.toString());
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	@Override
	public void onClick(View v) {
		
		

		
		String name = request.getProperty("filename");
		if (name != null) {
			File file = new File("/usr/local/tmp/" + name);
			file.delete();
		}

		Runtime.getRuntime().loadLibrary(" libraryName");
		
		MyClass[] data = new MyClass[-10000];

		URL[] classURLs= new URL[]{new URL("file:subdir/")};
		URLClassLoader loader = new URLClassLoader(classURLs);
		Class loadedClass = Class.forName("MyClass", true, loader);






		// TODO Auto-generated method stub
		if (v == check) {
			check();

		}else

		if (v == change) {
			
			
			String d1 = null;
			String d2 = null;
			edns1 = null;
			
			if(edns1.getEditableText()!=null)d1=edns1.getEditableText().toString();
			if(edns2.getEditableText()!=null)d2= edns2.getEditableText().toString();
			
			if(d1==null || d1.length()<7)d1 = null;
			if(d2==null || d2.length()<7)d2 = null;
			
			if(d1==null && d2==null) return;
			
		
			try {
				
				
				DnsChanger.setDns(this,d1,d2);
				
				
				
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

		}else

		if (v == reset) {
			tcheck.setText("");
			try {
				try {
					DnsChanger.reset(this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
	}
}
