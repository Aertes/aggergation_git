package com.citroen.wechat.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.citroen.ledp.util.PropertiyUtil;

/** 
 * @ClassName: FileUtil 
 * @Description: 处理文件的类 
 * @author 杨少波
 * @date 2015年6月11日 下午4:48:41 
 * 
 */
public class FileUtil {

	public static void main(String[] args) {
		writeOverRideFile("d:/index.edit","aa");
	}
	public static void copy(String oldFile,String newFile,String code,String name){
		 	(new File(newFile)).mkdirs();
	        // 获取源文件夹当前下的文件或目录
	        File[] file = (new File(oldFile)).listFiles();
	        for (int i = 0; i < file.length; i++) {
	            if (file[i].isFile()) {
	                // 复制文件
	                String type = file[i].getName().substring(file[i].getName().lastIndexOf(".") + 1);
	                    try {
							copyFile(file[i], new File(newFile + file[i].getName()),newFile + file[i].getName(),code,name);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            }
	            if (file[i].isDirectory()) {
	                // 复制目录
	                String sourceDir = oldFile + File.separator + file[i].getName();
	                String targetDir = newFile + File.separator + file[i].getName();
	                try {
						copyDirectiory(sourceDir, targetDir,code,name);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }
	}
	/**
	 * @functionName: copyFile 
	 * @Description: 复制另个文件 
	 * @author 杨少波
	 * @date 2015年6月11日 下午4:48:41  
	 * oldPath为要复制的文件路径，url2为新文件的路径
	 */
	
	 public static void copyFile(File sourceFile, File targetFile,String path,String code,String filePath) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
        
        if(path.substring(path.lastIndexOf(".")+1).equals("html")){
        	String type=filePath;
        	String html=readFile(path);
	        PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String saveDir ="";
			if(type.equals("campaign")){
				saveDir=propUtil.getString("campaign.url");
			}else{
				saveDir=propUtil.getString("site.url");
			}
			
	        html=html.replaceAll("href=\"css", "href=\""+saveDir+"/"+code+"/css");
	        html=html.replaceAll("src=\"js", "src=\""+saveDir+"/"+code+"/js");
	        html=html.replaceAll("src=\"image", "src=\""+saveDir+"/"+code+"/image");
	        
	        Document doc = Jsoup.parse(html);
	        writeFile(path,doc.html());
        }
        
    }
	 public static void updateFile(String path,long id,String code,String name){
		 PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		 String saveDir="";
		 if(name.equals("site")){
			 saveDir=propUtil.getString("site.dir");
		}else{
			saveDir = propUtil.getString("campaign.dir");
		}
	 	String html=readFile(path);
	 	String js=readFile(saveDir+"/"+code+"/modalPageTemplate.text");
	 	html=html.replaceAll("<body>", "<body><input type='hidden' id='liulanPageId' name='liulanPageId' value='"+id+"'>");
	 	html=html.replaceAll("<head>", "<head>"+js);
	 	Document doc = Jsoup.parse(html);
	 	Elements links=doc.getElementsByClass("replaceplugin");
	 	for (Element link : links) {
	 		String type=link.attr("replacetype");
	 		if(type.equals("nav2")){
	 			Elements elements=link.children();
				for (Element element : elements) {
					element.child(0).attr("href",element.child(0).attr("replacevalue"));
				}
			}else if(type.equals("nav1")){
				Elements elements=link.child(0).child(0).children();
				for (Element element : elements) {
					element.child(0).attr("href",element.child(0).attr("replacevalue"));
				}
			}else if(type.equals("script_newcarlist")){
				String newCarListjs=readFile(saveDir+"/"+code+"/newCarList.txt");
				link.html("");
				link.html(newCarListjs);
			}else if(type.equals("script_newcar")){
				String newCarjs=readFile(saveDir+"/"+code+"/newCar.txt");
				link.html("");
				link.html(newCarjs);
			}else if(type.equals("newCarDetail_button")){
				String newCarjs=readFile(saveDir+"/"+code+"/newCarDetail.txt");
				link.html("");
				link.html(newCarjs);
			}
	 	}
	    writeFile(path,doc.html());
	 }
	// 复制文件夹
	    public static void copyDirectiory(String sourceDir, String targetDir,String code,String filePath) throws IOException {
	        // 新建目标目录
	        (new File(targetDir)).mkdirs();
	        // 获取源文件夹当前下的文件或目录
	        File[] file = (new File(sourceDir)).listFiles();
	        for (int i = 0; i < file.length; i++) {
	            if (file[i].isFile()) {
	                // 源文件
	                File sourceFile = file[i];
	                // 目标文件
	                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
	                copyFile(sourceFile, targetFile,new File(targetDir).getAbsolutePath() + File.separator + file[i].getName(),code,filePath);
	            }
	            if (file[i].isDirectory()) {
	                // 准备复制的源文件夹
	                String dir1 = sourceDir + "/" + file[i].getName();
	                // 准备复制的目标文件夹
	                String dir2 = targetDir + "/" + file[i].getName();
	                copyDirectiory(dir1, dir2,code,filePath);
	            }
	        }
	    }
	    /** @functionName: copyFile 
		 * @Description: 复制单个文件 
		 * @author 杨少波
		 * @date 2015年6月11日 下午4:48:41  
		 * oldPath为要复制的文件路径，url2为新文件的路径
		 */
		
		public static void copyFile(String oldPath,String newPath){
			try { 
				
				int bytesum = 0; 
				int byteread = 0; 
				File oldfile = new File(oldPath); 
				if (oldfile.exists()) { //文件存在时 
					InputStream inStream = new FileInputStream(oldPath); //读入原文件 
					FileOutputStream fs = new FileOutputStream(newPath); 
					byte[] buffer = new byte[1444]; 
					int length; 
					while ( (byteread = inStream.read(buffer)) != -1) { 
						bytesum += byteread; //字节数 文件大小 
						fs.write(buffer, 0, byteread); 
					} 
					fs.close();
					inStream.close(); 
				} 
			}catch (Exception e) { 
				e.printStackTrace(); 

			} 
		}
	/**
	 * @functionName: creatFolder 
	 * @Description: 创建文件夹
	 * @author 杨少波
	 * @date 2015年6月11日 下午4:48:41  
	 * path为创建文件夹的路径
	 */
	public static void creatFolder(String path){
		File file=new File(path);
		if(!file.exists()  && !file.isDirectory()){
			file.mkdir();
		}else{
			//System.out.println("文件夹存在");
		}
	}
	/**
	 * @functionName: readFile 
	 * @Description: 读取文件
	 * @author 杨少波
	 * @date 2015年6月11日 下午4:48:41  
	 * path为文件的路径
	 */
	public static String readFile(String path){
		String content ="";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				content += line;
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	/**
	 * @functionName: writeFile 
	 * @Description: 写入文件
	 * @author 杨少波
	 * @date 2015年6月11日 下午4:48:41  
	 * path为文件的路径,str为要写的内容
	 */
	public static void writeFile(String path,String str){
		BufferedWriter bw = null;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		
		try {
			fos = new FileOutputStream(new File(path),false);
			osw = new OutputStreamWriter(fos,"utf-8");
			bw = new BufferedWriter(osw);
			bw.write(str);
			bw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 覆盖原来的文件内容
	 * @param path
	 * @param str
	 */
	public static void writeOverRideFile(String path,String str){
		BufferedWriter out = null;
		try {
	         out = new BufferedWriter(new FileWriter(path));
	         out.write(str);
	         out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void deleteFile(String path){
		File file=new File(path);
		if(file.exists()){
			file.delete();
		}
	}
	public static String getNowDateH(){
		long time=System.currentTimeMillis();
		return Long.toString(time);
	}
	public static boolean deleteFiles(File dir){
		if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteFiles(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
	}
}
