package com.prolambda.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class FileService {
	
	private Document document = null;
	
	public FileService(){
		
	}
	
	public FileService(String xmlPath){
		readXML(xmlPath);
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

    public void zip(ZipOutputStream out,ArrayList<File> input,ArrayList<String> inputName) throws IOException{
    	FileInputStream fis = null;
    	BufferedInputStream bis = null; 
    	
    	for(int i=0;i<input.size();i++){
    		File file = input.get(i);
    		if(!file.exists()){
    			continue;
    		}
    		String name = inputName.get(i);
    		ZipEntry zipEntry = new ZipEntry(name);
    		out.putNextEntry(zipEntry);  
    		fis = new FileInputStream(file);  
            bis = new BufferedInputStream(fis);  
            int read = 0;  
            byte[] bufs = new byte[1024];  
            while((read=bis.read(bufs)) != -1) {  
            	//fileSize += read;
                out.write(bufs, 0, read);
                out.flush();
            }  
    	}
    	
    	if(null != fis){
			fis.close();
		}
		if(null != bis){
			bis.close();
		}
    }

    public Document readXML(String path) {
    	
    	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new File(path));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return document;
    }
    
    public Document writeXML(){
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
    	  DocumentBuilder builder = null; 
    	  try { 
    	   builder = dbf.newDocumentBuilder(); 
    	  } catch (Exception e) { 
    	  } 
    	  document = builder.newDocument(); 
    	  return document;
    }
    
    public String getContextParam(String paramName){
    	//Document document = readXML(xmlPath);
    	
    	NodeList nodes = document.getElementsByTagName("context-param");
    	//System.out.println("param size:"+nodes.getLength());
    	for(int i=0;i<nodes.getLength();i++){
    		Node node = nodes.item(i);
    		NodeList param = node.getChildNodes();
    		Node name = null;
    		for(int j=0;j<param.getLength();j++){
    			Node n = param.item(j);
        		
        		if(n.getNodeType()==Node.ELEMENT_NODE){
        			//Element en = (Element)n;
        			if("param-name".equals(n.getNodeName())){
        				name = n;
        				
        			}else if("param-value".equals(n.getNodeName())){
        				if(paramName.equals(name.getFirstChild().getNodeValue())){
        	    			//System.out.println("equals");
        	    			//n.getFirstChild().setNodeValue(paramValue);
        	    			return n.getFirstChild().getNodeValue();
        	    		}
        			}
        			
        		}
    		}
    		//System.out.println("name:"+name.getFirstChild().getNodeValue());
    		//System.out.println("value:"+value.getFirstChild().getNodeValue());
    	}
    	//doc2Xml(document,xmlPath);
    	return null;
    }
    
    public String getStatus(){
    	NodeList nodes = document.getElementsByTagName("Status");
    	Node node = nodes.item(0);
    	return node.getFirstChild().getNodeValue();
    }
    
    public String getStartTime(){
    	NodeList nodes = document.getElementsByTagName("StartTime");
    	Node node = nodes.item(0);
    	return node.getFirstChild().getNodeValue();
    }
    
    public String getLastTime(){
    	NodeList nodes = document.getElementsByTagName("LastTime");
    	Node node = nodes.item(0);
    	return node.getFirstChild().getNodeValue();
    }
    
    public String getBackupPath(){
    	NodeList nodes = document.getElementsByTagName("BackupPath");
    	Node node = nodes.item(0);
    	return node.getFirstChild().getNodeValue();
    }
    
    public String getBackupRule(){
    	NodeList nodes = document.getElementsByTagName("BackupRule");
    	Node node = nodes.item(0);
    	return node.getFirstChild().getNodeValue();
    }
    
    public String getCopyCount(){
    	NodeList nodes = document.getElementsByTagName("CopyCount");
    	Node node = nodes.item(0);
    	return node.getFirstChild().getNodeValue();
    }
    
    public void setContextParam(String paramName,String paramValue){
    	//Document document = readXML(xmlPath);
    	
    	NodeList nodes = document.getElementsByTagName("context-param");
    	//System.out.println("param size:"+nodes.getLength());
    	for(int i=0;i<nodes.getLength();i++){
    		Node node = nodes.item(i);
    		NodeList param = node.getChildNodes();
    		Node name = null;
    		for(int j=0;j<param.getLength();j++){
    			Node n = param.item(j);
        		
        		if(n.getNodeType()==Node.ELEMENT_NODE){
        			//Element en = (Element)n;
        			if("param-name".equals(n.getNodeName())){
        				name = n;
        				
        			}else if("param-value".equals(n.getNodeName())){
        				if(paramName.equals(name.getFirstChild().getNodeValue())){
        	    			//System.out.println("equals");
        	    			n.getFirstChild().setNodeValue(paramValue);
        	    			
        	    		}
        			}
        			
        		}
    		}
    		//System.out.println("name:"+name.getFirstChild().getNodeValue());
    		//System.out.println("value:"+value.getFirstChild().getNodeValue());
    	}
    	//doc2Xml(xmlPath);
    }
    
    public void doc2Xml(String xmlPath){
    	try {
           
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
           
            // transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(xmlPath));
            transformer.transform(source, result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //return flag;
    }
    
    public void setLastTime(String time){
    	NodeList nodes = document.getElementsByTagName("LastTime");
    	Node node = nodes.item(0);
    	//String value = node.getFirstChild().getNodeValue();
    	//System.out.println("Value:"+value);
    	node.getFirstChild().setNodeValue(time);
    }
    
    public void setStatus(String status){
    	NodeList nodes = document.getElementsByTagName("Status");
    	Node node = nodes.item(0);
    	//String value = node.getFirstChild().getNodeValue();
    	//System.out.println("Value:"+value);
    	node.getFirstChild().setNodeValue(status);
    	//value = node.getFirstChild().getNodeValue();
    	//System.out.println("Value:"+value);
    }
    
    public void writeBackupXML(String status,String start,String last,String path,String rule,String count){
    	Element root = document.createElement("Backup");
    	document.appendChild(root);
    	
    	Element statusEle = document.createElement("Status");
    	root.appendChild(statusEle);
    	Text tstatus = document.createTextNode(status);
    	statusEle.appendChild(tstatus);
    	
    	Element startEle = document.createElement("StartTime");
    	root.appendChild(startEle);
    	Text tstart = document.createTextNode(start);
    	startEle.appendChild(tstart);
    	
    	Element lastEle = document.createElement("LastTime");
    	root.appendChild(lastEle);
    	Text tlast = document.createTextNode(last);
    	lastEle.appendChild(tlast);
    	
    	Element pathEle = document.createElement("BackupPath");
    	root.appendChild(pathEle);
    	Text tpath = document.createTextNode(path);
    	pathEle.appendChild(tpath);
    	
    	Element ruleEle = document.createElement("BackupRule");
    	root.appendChild(ruleEle);
    	Text trule = document.createTextNode(rule);
    	ruleEle.appendChild(trule);
    	
    	Element copyEle = document.createElement("CopyCount");
    	root.appendChild(copyEle);
    	Text tcopy = document.createTextNode(count);
    	copyEle.appendChild(tcopy);
    }

    static class CompratorByLastModified implements Comparator<File>  
    {  
    	public int compare(File f1, File f2) {  
      long diff = f1.lastModified()-f2.lastModified();  
          if(diff>0)  
            return 1;  
          else if(diff==0)  
            return 0;  
          else  
            return -1;  
          }  
   
    	public boolean equals(Object obj){  
      
    		return true;  
    	}  
    }
}
