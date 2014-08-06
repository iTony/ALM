package com.prolambda.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.prolambda.controller.LibraryService;
import com.prolambda.controller.ProductVersionService;
import com.prolambda.model.ComponentVersion;
import com.prolambda.model.ComponentVersionList;
import com.prolambda.model.GuidCreator;
import com.prolambda.model.Library;
import com.prolambda.model.LibraryList;

public class PackageFileServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4466474242497329975L;
	private String strFileFolder;
	private String innoSetup;
	private String tempPath;
	String strRet = "";
	public void init()throws ServletException{
		strFileFolder = getServletContext().getInitParameter("strFileFolder");
		//ServletConfig config = getServletConfig();
		//strFileFolder = config.getInitParameter("strFileFolder");
		innoSetup = getServletContext().getInitParameter("innoSetup");
		File file = new File(strFileFolder);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		
		super.init();
	}
	
	public void destroy() {  
		
        super.destroy(); // Just puts "destroy" string in log  
        // Put your code here  
    }  
  
    public void doGet(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        doPost(request, response);  
    }  
  
    @SuppressWarnings({ "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {
    	
    	request.setCharacterEncoding("utf-8");  //���ñ���  
        System.out.println("upload");
    	
    	
    	//ArrayList<String> issList = new ArrayList<String>();
        //��ô����ļ���Ŀ����  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        //��ȡ�ļ���Ҫ�ϴ�����·��  
        //String path = request.getRealPath("/upload");  
          
        //���û�����������õĻ����ϴ���� �ļ� ��ռ�� �ܶ��ڴ棬  
        //������ʱ��ŵ� �洢�� , ����洢�ң����Ժ� ���մ洢�ļ� ��Ŀ¼��ͬ  
        /** 
         * ԭ�� �����ȴ浽 ��ʱ�洢�ң�Ȼ��������д�� ��ӦĿ¼��Ӳ���ϣ�  
         * ������˵ ���ϴ�һ���ļ�ʱ����ʵ���ϴ������ݣ���һ������ .tem ��ʽ��  
         * Ȼ���ٽ�������д�� ��ӦĿ¼��Ӳ���� 
         */
        GuidCreator gc = new GuidCreator();
    	String guidPath = gc.toString();
		tempPath = strFileFolder + "/" + guidPath;
		
        File tempDir = new File(tempPath);
        if(!tempDir.exists()){
        	tempDir.mkdirs();
        }
        factory.setRepository(tempDir);  
        //���� ����Ĵ�С�����ϴ��ļ������������û���ʱ��ֱ�ӷŵ� ��ʱ�洢��  
        factory.setSizeThreshold(1024*1024) ;  
          
        //��ˮƽ��API�ļ��ϴ�����  
        ServletFileUpload upload = new ServletFileUpload(factory);  
          
        strRet = "";
        
        try {  
            //�����ϴ�����ļ�  
            List<FileItem> list = (List<FileItem>)upload.parseRequest(request);
            if(list.size()<1){
            	return;
            }
            createFolder(request,response);
            System.out.println(list.size());
            for(int i=0;i<list.size();i++)  
            {  
            	FileItem item = list.get(i);
                //��ȡ������������  
            	//System.out.println("i:"+i);
                String name = item.getFieldName();  
                System.out.println("name:"+name);
                //�����ȡ�� ����Ϣ����ͨ�� �ı� ��Ϣ  
                if(item.isFormField())  
                {                     
                    //��ȡ�û�����������ַ��� ���������ͦ�ã���Ϊ���ύ�������� �ַ������͵�  
                    String value = item.getString();
                    //System.out.println("name:"+name+" value:"+value);
                    if(name.equals("checkbox")&&value.equals("no")){
                    	i+=1;
                    	continue;
                    }
                    FileItem textItem = list.get(++i);
                   // FileItem pathItem = list.get(++i);
                    if(textItem.isFormField()){
                    	String libId = textItem.getString();
                        System.out.println("libId:"+libId);
                        LibraryService libSer = new LibraryService();
                        Library lib = libSer.getById(Integer.parseInt(libId));
                        String fileName = lib.getName();
                    //    String path = pathItem.getString();
                    //    int index = fileName.lastIndexOf("/");
                    //    if(path.equals(fileName.substring(0,index))){
                   //     	continue;
                    //    }
                       // fileName = path + fileName.substring(index+1);
                    //    lib.setName(fileName);
                        File sourceFile = new File(strFileFolder+"/"+lib.getFileName());
                        File targetFile = new File(tempPath+"/"+fileName);
                       
                        if(sourceFile.exists()){
                        	
                        	copyFile(sourceFile,targetFile);
                        	packageIss(fileName);
                        	addLibrary(request,response,fileName);
                        }
                    }else{
                    	
                         /** 
                          * ������������Ҫ��ȡ �ϴ��ļ������� 
                          */  
                         //��ȡ·����  
                     	
                     	
                     	//String path = pathItem.getString();
                     	//path = path.replace('\\', '/');
                     //	System.out.println(path);
                     	
                        String itemValue = textItem.getName();
                         //System.out.println(path+"/"+value);
                         //���������һ����б��  
                        int start = itemValue.lastIndexOf("\\");  
                         //��ȡ �ϴ��ļ��� �ַ������֣���1�� ȥ����б�ܣ�  
                         String filename = itemValue.substring(start+1);  
                         //System.out.println("filename:"+filename);
                         //issList.add(filename);
                         //request.setAttribute(name, filename);  
                         
                         //����д��������  
                         //���׳����쳣 ��exception ��׽  
                           
                         //item.write( new File(path,filename) );//�������ṩ��  
                           
                         //�ֶ�д��  
                         File issPath = new File(tempPath);
                         if(!issPath.exists()){
                         	issPath.mkdirs();
                         }
                         File issFile = new File(tempPath,filename);
                         
                         OutputStream out = new FileOutputStream(issFile);  
                           
                         InputStream in = textItem.getInputStream() ;  
                           
                         int length = 0 ;  
                         byte [] buf = new byte[1024] ;  
                           
                        /// System.out.println("file:"+tempPath+"/"+filename);
       
                         // in.read(buf) ÿ�ζ��������ݴ����   buf ������  
                         while( (length = in.read(buf) ) != -1)  
                         {  
                             //��   buf ������ ȡ������ д�� ���������������  
                             out.write(buf, 0, length);  
                               
                         }  
                         
                         in.close();  
                         out.flush();
                         out.close();  
                         
                         packageIss(filename);
                         addLibrary(request,response,filename);
                         
                    }
                    
                  
                }  
                //�Դ���ķ� �򵥵��ַ������д��� ������˵�����Ƶ� ͼƬ����Ӱ��Щ  
        
            }
            
    		
        } catch (FileUploadException e) {  
            //   
            e.printStackTrace();  
        }  
        catch (Exception e) {  
            //   
              
            //e.printStackTrace();  
        }  
        
        delFolder(tempPath);
       
        HttpSession session = request.getSession(true);
        PrintWriter out = response.getWriter();
        
        String versionId = (String)session.getAttribute("productVersionId");
        ProductVersionService pVerSer = new ProductVersionService();
    	LibraryService libSer = new LibraryService();
		LibraryList setupFileList = libSer.getAllByProductId(Integer.parseInt(versionId));	
		
		LibraryList libList = new LibraryList();
		for(Library tlib:setupFileList){
			int tfileId = pVerSer.getIssBySetupFile(tlib.getId());
			Library issFile = libSer.getById(tfileId);
			libList.add(issFile);
		}
		
		session.setAttribute("issFileList", libList);
		session.setAttribute("setupFileList", setupFileList);
		
		//System.out.println("strRet:"+strRet);
		out.print("<script>parent.callback('"+strRet+"')</script>");
		
    }  
    
    public void addLibrary(HttpServletRequest request,HttpServletResponse response,String filename) throws ServletException, IOException{
    	
    	System.out.println("filename:"+filename);
    	File issFile = new File(tempPath+"/"+filename);
    	HttpSession session = request.getSession(true);
    	String versionId = (String)session.getAttribute("productVersionId");
    	
    	ProductVersionService pVerSer = new ProductVersionService();
    	LibraryService libSer = new LibraryService();
    	BufferedReader reader = null;
    	
    	GuidCreator gc = new GuidCreator();
    	
    	String defaultPath = ".";
    	String sourceDir = ".";
    	String path = ".";
        String setupFile = "setup.exe";
        Boolean flag = true;
        
        File issSourceFile = new File(tempPath+"/"+filename);
        String guidName = gc.toString();
        File issTargetFile = new File(strFileFolder+"/"+guidName);
        
        
        
    	try {
            //System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
            reader = new BufferedReader(new FileReader(issFile));
            String tempString = null;
           
            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����
            while ((tempString = reader.readLine()) != null) {
                // ��ʾ�к�
               int index = tempString.indexOf("OutputDir");
               if(index!=-1){
            	  index = tempString.indexOf("=");
            	  if(index!=-1){
            		  path = tempString.substring(index+1).trim();
            	  }
               }else{
            	   index = tempString.indexOf("OutputBaseFilename");
                   if(index!=-1){
                	   index = tempString.indexOf("=");
                	   if(index!=-1){
                		   setupFile = tempString.substring(index+1).trim()+".exe";
                	   }
                   }else{
                	   index = tempString.indexOf("SourceDir");
                	   if(index!=-1){
                		   index = tempString.indexOf("=");
                		   if(index!=-1){
                			   sourceDir = tempString.substring(index+1).trim();
                		   }
                	   }
                	   /*
                	   else {
                		   index = tempString.indexOf("DefaultGroupName");
                		   if(index!=-1){
                			   index = tempString.indexOf("=");
                			   if(index!=-1){
                				   defaultPath = tempString.substring(index+1).trim();
                			   }
                		   }
                	   }
                	   */
                   }
               }
               
            }
            //System.out.println("path:"+path);
            
            if(path!=null){
            	String sourcePath = "";
            	int index = path.indexOf(":");
            	if(index!=-1){
            		sourcePath = path;
            	}else {
            		sourcePath = tempPath + "/" /*+defaultPath + "/"*/ + sourceDir + "/" +path + "/" + setupFile;
            	}
            	System.out.println("DefaultPath:"+defaultPath);
            	System.out.println("SourceDir:"+sourceDir);
            	System.out.println("Path:"+path);
            	System.out.println("SetupFile:"+setupFile);
            	System.out.println("Path:"+sourcePath);
            	//System.out.println(path+"/"+setupFile);
            	File sourceFile = new File(sourcePath);
            	gc = new GuidCreator();
            	String setupGuidName = gc.toString();
            	//System.out.println(guidName);
            	File targetFile = new File(strFileFolder+"/"+setupGuidName);
            	if(sourceFile.exists())
            		copyFile(sourceFile,targetFile);
            	else {
            		System.out.println("Create setup file error");
            		return;
            	}
            	
            	Library ilib = new Library();
            	
                int libId = 0;
                
            	index = filename.lastIndexOf('.');
        		String type = filename.substring(index);
        		//path = fullPath + path;
        		//System.out.println("issName:"+filename);
        		ilib.setName(filename);
        		ilib.setType(type);
        		//ilib.setProductId(Integer.parseInt(versionId));
        		ilib.setFileName(guidName);
               // System.out.println("guidName:"+guidName);
                copyFile(issSourceFile,issTargetFile);
               // System.out.println("issSourceFile:"+issSourceFile.getName());
                LibraryList libList = (LibraryList)session.getAttribute("issFileList");
              //  System.out.println("libList:"+libList);
        		for(Library tlib:libList){
        			if(tlib.getName().equals(filename)){
        				//tlib.setId(lib.getId());
        				libId = tlib.getId();
        				ilib.setId(libId);
        				libSer.update(ilib);
        				//System.out.println("oldFile: "+tlib.getFileName());
        				File oldFile = new File(strFileFolder+"/"+tlib.getFileName());
        				if(oldFile.exists()){
        					oldFile.delete();
        				}
        				flag = false;
        			}
        		}
        		if(flag){
        			System.out.println("create issFile");
        			libSer.create(ilib);
        			libId = libSer.getIdByFileName(guidName);
        		}
            	
            	Library lib = new Library();
            	
            	
            	//index = setupFile.lastIndexOf('.');
				//type = setupFile.substring(index);
				//path = fullPath + path;
            	String setupName = filename.substring(0, index+1)+"exe";
				lib.setName(setupName);
				lib.setType(".exe");
				lib.setProductId(Integer.parseInt(versionId));
				lib.setFileName(setupGuidName);
				//libSer.create(lib);
				//System.out.println("issFileId:"+libId);
				if(pVerSer.containsIss(libId)){
					int fileId = pVerSer.getFileByIss(libId);
					//System.out.println("libId:"+libId+" fileId:"+fileId);
					if(fileId!=-1){
						Library oldLib = libSer.getById(fileId);
						File oldFile = new File(strFileFolder+"/"+oldLib.getFileName());
						if(oldFile.exists()){
							oldFile.delete();
						}
						lib.setId(fileId);
						libSer.update(lib);
						strRet += "<a href=\"LibraryServlet?flag=download&libId="+libId+"\">"+filename+"</a>" +
								"<a href=\"LibraryServlet?flag=download&libId="+fileId+"\">"+setupName+"</a>";
					}else {
						//System.out.println("create setupFile:");
						libSer.create(lib);
						fileId = libSer.getIdByFileName(setupGuidName);
						//System.out.println("libId:"+libId+" fileId:"+fileId);
						pVerSer.addIssFile(Integer.parseInt(versionId),libId, fileId);
						strRet += "<a href=\"LibraryServlet?flag=download&libId="+libId+"\">"+filename+"</a>" +
								"<a href=\"LibraryServlet?flag=download&libId="+fileId+"\">"+setupName+"</a>";
					}
				}else{
					System.out.println("create setupFile:");
					libSer.create(lib);
					int fileId = libSer.getIdByFileName(setupGuidName);
					//System.out.println("libId:"+libId+" fileId:"+fileId);
					pVerSer.addIssFile(Integer.parseInt(versionId),libId, fileId);
					strRet += "<a href=\"LibraryServlet?flag=download&libId="+libId+"\">"+filename+"</a>" +
							"<a href=\"LibraryServlet?flag=download&libId="+fileId+"\">"+setupName+"</a>";
				}
            }
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        
        
		
    }
    
    public void packageIss(String filename) throws IOException{
    	
    	Runtime runtime = Runtime.getRuntime();
    	
    	Process process = runtime.exec(innoSetup+" /cc \""+tempPath+"/"+filename+"\"");
 		
 		try {
 			
 			process.waitFor();
 			if(process.exitValue()!=0){
 				System.out.print("failed");
 			}
 			
 		} catch (InterruptedException e) {
 			// 
 			e.printStackTrace();
 		}
 		
    }

    public void delFolder(String folderPath) {
        try {
        	File myFilePath = new File(folderPath);
        	if(myFilePath.exists()){
        		delAllFile(folderPath); //ɾ����������������
              
                myFilePath.delete(); //ɾ�����ļ���
        	}
           
        } catch (Exception e) {
        	e.printStackTrace(); 
        }
    }
    
    public boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
          return flag;
        }
        if (!file.isDirectory()) {
          return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
           if (path.endsWith(File.separator)) {
              temp = new File(path + tempList[i]);
           } else {
               temp = new File(path + File.separator + tempList[i]);
           }
           if (temp.isFile()) {
              temp.delete();
           }
           if (temp.isDirectory()) {
              delAllFile(path + "/" + tempList[i]);//��ɾ���ļ���������ļ�
              delFolder(path + "/" + tempList[i]);//��ɾ�����ļ���
              flag = true;
           }
        }
        return flag;
      }
    public void createFolder(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(true);
		
    	
		
		String versionId = (String)session.getAttribute("productVersionId");
		
		
		ProductVersionService versionSer = new ProductVersionService();
		ComponentVersionList versionList = versionSer.getComponents(Integer.parseInt(versionId));
		
		for(ComponentVersion comVersion:versionList){
			String location = versionSer.getComponentLocation(Integer.parseInt(versionId), comVersion.getId());
			//Component com = comSer.getById(comVersion.getComponentId());
			//ComponentCategory cCategory = cCatSer.getById(com.getCategoryId());
			//strRet += cCategory.getName()+","+com.getName()+","+comVersion.getVersion()+",";
			findFile(comVersion.getId(),location);
			//strRet += "|";
		}
    }
    
    private void findFile(int id,String location) throws IOException{
    	LibraryService libSer = new LibraryService();
    	LibraryList libList = libSer.getAllByComponentId(id);
		for(Library lib:libList){
			String name = lib.getName();
			int index = name.lastIndexOf('/');
			String path = name;
			String fileName = name;
			//System.out.println("name:"+name);
			File file = null;
			if(index!=-1){
				path = location + "/" +name.substring(0,index);
				
				fileName = name.substring(index+1);
				file = new File(tempPath+"/"+path);
				if (!file.exists()) {
					file.mkdirs();
				}
			}else{
				
				file = new File(tempPath+"/"+location);
				
				if (!file.exists()) {
					file.mkdirs();
				}
			}
			
			
			File sourceFile = new File(strFileFolder+"/"+lib.getFileName());
			File targetFile = new File(file.getAbsolutePath()+"/"+fileName);
			if(sourceFile.exists()){
				copyFile(sourceFile,targetFile);
			}
			
			//strRet += lib.getName()+",";
		}
    }
    public void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // �½��ļ����������������л���
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // �½��ļ���������������л���
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // ��������
            byte[] b = new byte[1024 * 10];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // ˢ�´˻���������
            outBuff.flush();
        } finally {
            // �ر���
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
}
