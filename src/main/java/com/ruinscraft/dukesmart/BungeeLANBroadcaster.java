package com.ruinscraft.dukesmart;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLANBroadcaster extends Plugin {
	private static final String BROADCAST_HOST = "224.0.2.60";
	private static final int BROADCAST_PORT = 4445;
	

    @Override
    public void onEnable() {
       
       getProxy().getScheduler().schedule(this, new Runnable() {
    	    private DatagramSocket socket;
			private int failcount;
			private InetSocketAddress host;
			private String motd;

			@Override
    	    public void run() {
    	    	try
    		    {
    				byte[] ad = getAd();
    				this.socket = new DatagramSocket();
    				DatagramPacket packet = new DatagramPacket(ad, ad.length, InetAddress.getByName(BROADCAST_HOST), BROADCAST_PORT);
    				broadcast(socket, packet);
    		    }
    		    catch (Exception e)
    		    {
    		    	e.printStackTrace();
    		    }
    		    this.socket.close();
    	    }

			private byte[] getAd() {
				String motd = getBungeeMotd();
			    InetSocketAddress host = getHost();
			    int port = host.getPort();
			    String str = "[MOTD]" + motd + "[/MOTD][AD]" + port + "[/AD]";
			    try
			    {
			      return str.getBytes("UTF-8");
			    }
			    catch (UnsupportedEncodingException ex)
			    {
			      ex.printStackTrace();
			    }
			    return str.getBytes();
			}

			private String getBungeeMotd() {
				ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
				for ( final ListenerInfo info : adapter.getListeners() )
		        {
					motd = info.getMotd();
		        }
				return motd;
			}

			private InetSocketAddress getHost() {
				ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
				for ( final ListenerInfo info : adapter.getListeners() )
		        {
					host = info.getHost();
		        }
				
				return host;
			}

			private void broadcast(DatagramSocket socket, DatagramPacket packet) {
				try
				{
					try
					{
						socket.send(packet);
						this.failcount = 0;
					}
					catch (Throwable ex)
					{
						fail(ex);
					}
				}
				catch (InterruptedException localInterruptedException)
				{
					
				}
				
			}

			private void fail(Throwable ex)
					throws InterruptedException
			{
				if (this.failcount++ == 0) {
				      ex.printStackTrace();
				    }
				    if (this.failcount < 5) {
				      getProxy().getLogger().warning("Failed to broadcast, trying again in 10 seconds...");
				    } else if (this.failcount == 5) {
				      getProxy().getLogger().severe("Broadcasting will not work until the network is fixed. Warnings disabled.");
				    }
				    Thread.sleep(8500L);
			}
    	  }, 50, 50, TimeUnit.MILLISECONDS);
    }
    
    public void onDisable() {
    	
    }

}