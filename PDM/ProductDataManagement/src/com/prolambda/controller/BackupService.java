package com.prolambda.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import com.prolambda.dal.BackupDAL;
import com.prolambda.model.BackupLog;

public class BackupService extends TimerTask{

	String dumpCmd;
	//String outputFile;
	//String fileBackup;
	String backuppath;
	String sourceDir;
	String type;
	String xmlPath;
	int count = 0;
	
	//String xmlPath;
	FileService fileSer = null;
	public BackupService(ServletContext context){
		xmlPath = context.getRealPath("/")+"WEB-INF/backup.xml";
		dumpCmd = context.getInitParameter("mysqldumpCmd");
		//outputFile = context.getInitParameter("datebaseBackup");
		//fileBackup = context.getInitParameter("fileBackup");
		sourceDir = context.getInitParameter("strFileFolder");
		
		fileSer = new FileService(xmlPath);
		backuppath = fileSer.getContextParam("path");
		
		//int index = path.lastIndexOf("/");
		//String temp = path.substring(0,index);
		//System.out.println(path);
		File file = new File(backuppath);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
	}
	public BackupService(ServletContext context,String path,String type){
		xmlPath = context.getRealPath("/")+"WEB-INF/backup.xml";
		//System.out.println("BackuService--->");
		//xmlPath = context.getRealPath("/")+"WEB-INF/backup.xml";
		dumpCmd = context.getInitParameter("mysqldumpCmd");
		//outputFile = context.getInitParameter("datebaseBackup");
		//fileBackup = context.getInitParameter("fileBackup");
		sourceDir = context.getInitParameter("strFileFolder");
		
		//fileSer = new FileService(xmlPath);
		//backuppath = fileSer.getContextParam("path");
		backuppath = path;
		this.type = type;
		
		//int index = path.lastIndexOf("/");
		//String temp = path.substring(0,index);
		//System.out.println(path);
		File file = new File(path);
		
		if(!file.exists()){
			file.mkdirs();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//String xmlPath = context.getRealPath("/")+"WEB-INF/backup.xml";
		
		FileService fileSer = new FileService(xmlPath);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String time = df.format(new Date());
		File file = new File(backuppath+"/"+time);
		if(!file.exists()){
			file.mkdirs();
		}
		File path = new File(backuppath);
		File[] files = path.listFiles();
		//System.out.println("Length:"+files.length);
		//System.out.println("Count:"+count);
		int copy = Integer.parseInt(fileSer.getCopyCount());
		if(files.length>copy&&copy!=0){
			Arrays.sort(files, new FileService.CompratorByLastModified());
			//System.out.println("File:"+files[0].getAbsolutePath());
			fileSer.delFolder(files[0].getAbsolutePath());
		}
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		fileSer.setLastTime(date.toString());
		String startTime = "";
		String endTime = "";
		String status = "success";
		String log = "";
		//String backupStr = backuppath+"/"+time+"/backupfile.zip";
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			startTime = df.format(new Date());
			if("File|Database".equals(type)){
				
				log += backupDataBase(time);
				
				log += backupFile(time);
			}else if("Database".equals(type)){
				log += backupDataBase(time);
			}else if("File".equals(type)){
				log += backupFile(time);
			}
			endTime = df.format(new Date());
		} catch (IOException e) {
			status = "error";
			e.printStackTrace();
		} finally{
			fileSer.doc2Xml(xmlPath);
			BackupLog blog = new BackupLog();
			blog.setStartTime(startTime);
			blog.setEndTime(endTime);
			blog.setStatus(status);
			blog.setLog(log);
			BackupDAL backupDAL = new BackupDAL();
			backupDAL.create(blog);
		}
	}
	
	public String backupFile(String time) throws IOException{
		FileService fileSer = new FileService();
		StringBuilder log = new StringBuilder("Start Backup File \n");
		File sourceFile = new File(sourceDir);
		File[] files = sourceFile.listFiles();
		log.append("From:").append(sourceFile.getAbsolutePath());
		ArrayList<File> input = new ArrayList<File>();
		ArrayList<String> name = new ArrayList<String>();
		for(int i=0;i<files.length;i++){
			File file = files[i];
			input.add(file);
			name.add(file.getName());
		}
		String backupStr = backuppath+"/"+time+"/backupfile.zip";
		log.append(" \nTo:").append(backupStr);
		//System.out.println("Str:"+backupStr);
		File backupFile = new File(backupStr);
		if(backupFile.exists()){
			backupFile.delete();
		}
		
		FileOutputStream fos = new FileOutputStream(backupStr);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ZipOutputStream zos = new ZipOutputStream(bos);
		fileSer.zip(zos, input, name);
		zos.finish();
		bos.flush();
		fos.flush();
		
		if(null != zos){
			
			zos.close();  
		}
		if(null != bos) {
			
			bos.close();  
		}
		
		if(null != fos){
			fos.close();
		}
		log.append("\nEnd Backup\n");
		return log.toString();
	}
	
	public String backupDataBase(String time){
		StringBuilder log = new StringBuilder("Start Backup Database\n");
		try {     
            Runtime rt = Runtime.getRuntime();     
    
            // ���� mysql �� cmd:     
            log.append("From: db_pdm\n");
            Process child = rt.exec("\""+dumpCmd +"\" -uroot -proot db_pdm");// ���õ�������Ϊutf8�����������utf8     
                
            // �ѽ���ִ���еĿ���̨�����Ϣд��.sql�ļ����������˱����ļ���ע��������Կ���̨��Ϣ���ж�������ᵼ�½��̶����޷�����     
            InputStream in = child.getInputStream();// ����̨�������Ϣ��Ϊ������     
                            
            InputStreamReader xx = new InputStreamReader(in, "utf8");// �������������Ϊutf8�����������utf8����������ж����������     
                
            String inStr;     
            StringBuffer sb = new StringBuffer("");
            String outStr;     
            // ��Ͽ���̨�����Ϣ�ַ���     
            BufferedReader br = new BufferedReader(xx);     
            while ((inStr = br.readLine()) != null) {     
                sb.append(inStr + "\r\n");     
            }     
            outStr = sb.toString();     
                
            String backupStr = backuppath+"/"+time+"/DBbackup.sql";
            log.append("To: ").append(backupStr);
            // Ҫ�����������õ�sqlĿ���ļ���     
            FileOutputStream fout = new FileOutputStream(backupStr);     
            OutputStreamWriter writer = new OutputStreamWriter(fout, "utf8");     
            writer.write(outStr);     
            // ע����������û��巽ʽд���ļ��Ļ����ᵼ���������룬��flush()��������Ա���     
            writer.flush();     
    
            // �����ǹر����������     
            in.close();     
            xx.close();     
            br.close();     
            writer.close();     
            fout.close();     
    
            //System.out.println("/* Output OK! */");     
    
        } catch (Exception e) {     
            e.printStackTrace();     
        }
		log.append("\nEnd Backup");
		return log.toString();
	}
}
