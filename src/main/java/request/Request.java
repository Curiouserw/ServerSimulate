package request;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class Request{
	// 请求方式
	private String method;
	// 请求内容
	private String url;
	// 协议
	private String protocol;
	// 存放请求体中的参数，并且拼成map集合
	private Map<String,String> map=new HashMap<String,String>();

	public String getURL(){
		return this.url;
	}

	public String getMethod(){
		return this.method;
	}

	public String getProtocol(){
		return protocol;
	}

	public void setProtocol(String protocol){
		this.protocol=protocol;
	}

	public Request(BufferedReader br) throws Exception{
		String temp="";
		temp=br.readLine();
		String[] str=temp.split(" ");
		method=str[0];
		url=str[1];
		protocol=str[2];
		int length=0;
		while(true){
			String httpHead=br.readLine();
			if(httpHead.indexOf("Content-Length")!=-1){
				String[] str1=httpHead.split(":");
				length=Integer.parseInt(str1[1].trim());
			}
			if(httpHead.equals("")){
				break;
			}
		}
		String info="";
		if(method.equals("POST")){
			for(int i=0;i<length;i++){
				info+=(char)br.read();
			}
			System.out.println(info);
			String[] str3=info.split("&");
			String[] str4=str3[0].trim().split("=");
			String[] str5=str3[1].trim().split("=");
			map.put(str4[0],str4[1]);
			map.put(str5[0],str5[1]);
		}
	}
}
