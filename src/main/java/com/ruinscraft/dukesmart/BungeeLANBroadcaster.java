package com.ruinscraft.dukesmart;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLANBroadcaster extends Plugin {
	private static final String BROADCAST_HOST = "224.0.2.60";
	private static final int BROADCAST_PORT = 4445;
	public Thread thread;
	

    @Override
    public void onEnable() {
       
       getProxy().getScheduler().runAsync(this, new Runnable() {
    	    private DatagramSocket socket;
			private int failcount;
			private InetSocketAddress host;

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
				String motd = ProxyServer.getInstance().getServerInfo("lobby").getMotd();
			    InetSocketAddress host = getHost();
			    /*String ip = host.getHostString();*/
			    int port = host.getPort();
			    String str = "[MOTD]" + motd + "[/MOTD][AD]" + port + "[/AD]";
			    getProxy().getLogger().info("Broadcasting: " + str);
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
					for (;;)
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
    	  });
    }
    
    public void onDisable() {
    	
    }

}