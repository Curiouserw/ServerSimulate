package response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import simu_anno.ActionAnnotation.Action;

public class Response{
	// socket的输出流
	private PrintStream out;
	// 用户的请求
	private String url;
	// 服务器路径
	private String path;
	// 欢迎文件
	private String welcomeURL;
	// 发生错误的页面
	private String errorURL;
	private static Properties properties=null;
	static{
		properties=new Properties();
		try{
			properties.load(new FileInputStream("prop/property.properties"));
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Response(PrintStream out){
		this.out=out;
		path="html";
		errorURL="/error.html";
		welcomeURL="/demo.jpg";
	}

	public void sendMessage(){
		try{
			if("/".equals(url)){
				// 此if判断的是加入访问的是不是服务器根目录，如果是服务器根目录，将路径进行重定向
				sendFileMessage(out,path,welcomeURL,false,null);
			}else{
				// 此if判断的是浏览器访问的是服务器资源还是servlet或者是action
				if(!url.contains(".")){
					url=url.substring(1);
					String servletClassName=properties.getProperty(url);
					Class<?> clazz=Class.forName(servletClassName);
					Object object=clazz.newInstance();
					Method method=clazz.getMethod(url.substring(0,url.length()-7));
					Object invoke=method.invoke(object);
					System.out.println();
					sendFileMessage(out,path,url,true,invoke.toString());
				}else{
					if(url.endsWith(".action")){
						properties.load(new FileInputStream(new File("prop/action.properties")));
						String actionClassPaths=properties.getProperty("package-scan");
						List<File> files=new ArrayList<File>();
						String[] actionClassPathsArray=actionClassPaths.split(",");
						for(String actionClassPath:actionClassPathsArray){
							File tmpFile=new File("src/"+actionClassPath+"/");
							if(tmpFile.exists()){
								String[] list=tmpFile.list();
								for(String string:list){
									files.add(new File(new File(tmpFile,string).getAbsolutePath()));
								}
								for(File file:files){
									if(file.getName().endsWith(".java")){
										String string=actionClassPath.replace("/",".")+"."+file.getName().substring(0,file.getName().length()-5);
										Class<?> clazz=Class.forName(string);
										Object object=clazz.newInstance();
										Method[] methods=clazz.getDeclaredMethods();
										for(Method method:methods){
											if(method.isAnnotationPresent(Action.class)&&(url.substring(1,url.length()-7).equals(method.getAnnotation(Action.class).name()))){
												sendFileMessage(out,path,url,true,method.invoke(object).toString());
											}else{
												sendErrorMessage(out,path,errorURL);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendFileMessage(PrintStream out,String path,String url,boolean flag,String msg){
		out.println("HTTP/1.1 200 OK");
		out.println();
		FileInputStream fs=null;
		BufferedInputStream bis=null;
		if(flag){
			out.write(msg.getBytes(),0,msg.getBytes().length);
			out.flush();
			out.close();
		}else{
			File file=new File(path,url);
			if(file.exists()){
				try{
					fs=new FileInputStream(file);
					bis=new BufferedInputStream(fs);
					byte[] buf=new byte[1024];
					int length=0;
					while((length=bis.read(buf))!=-1){
						out.write(buf,0,length);
						out.flush();
					}
					out.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sendErrorMessage(PrintStream out,String path,String errorURL){
		out.println("HTTP/1.1 404 ERROR");
		out.println();
		File file1=new File(path,errorURL);
		FileInputStream fs2=null;
		BufferedInputStream bis1=null;
		try{
			fs2=new FileInputStream(file1);
			bis1=new BufferedInputStream(fs2);
			byte[] buf2=new byte[1024];
			int length=0;
			while((length=bis1.read(buf2))!=-1){
				out.write(buf2,0,length);
				out.flush();
			}
			out.close();
		}catch(Exception e1){
			e1.printStackTrace();
		}
	}

	public void sendMessage(String url){
		if(url!=null){
			this.url=url;
			sendMessage();
		}
	}
}
