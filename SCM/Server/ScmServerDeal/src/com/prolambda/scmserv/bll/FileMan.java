package com.prolambda.scmserv.bll;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileMan {

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // �½��ļ����������������л���
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // �½��ļ���������������л���
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // ��������
            byte[] b = new byte[1024 * 5];
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
	
	public static void copyDirectiory(String sourceDir, String targetDir)  {
        // �½�Ŀ��Ŀ¼
        (new File(targetDir)).mkdirs();
        // ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // Դ�ļ�
                File sourceFile = file[i];
                // Ŀ���ļ�
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                try {
					copyFile(sourceFile, targetFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (file[i].isDirectory()) {
                // ׼�����Ƶ�Դ�ļ���
                String dir1 = sourceDir + "/" + file[i].getName();
                // ׼�����Ƶ�Ŀ���ļ���
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
	
	public static void cutDirectiory(String sourceDir,String targetDir){
		(new File(targetDir)).mkdirs();
        // ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // Դ�ļ�
                File sourceFile = file[i];
                // Ŀ���ļ�
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                try {
					copyFile(sourceFile, targetFile);
					del(sourceFile.getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (file[i].isDirectory()) {
                // ׼�����Ƶ�Դ�ļ���
                String dir1 = sourceDir + "/" + file[i].getName();
                // ׼�����Ƶ�Ŀ���ļ���
                String dir2 = targetDir + "/" + file[i].getName();
                cutDirectiory(dir1, dir2);
            }
        }
	}
	
	public static void copyFile(File srcFileName, File destFileName, String srcCoding, String destCoding) throws IOException {// ���ļ�ת��ΪGBK�ļ�
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), srcCoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), destCoding));
            char[] cbuf = new char[1024 * 5];
            int len = cbuf.length;
            int off = 0;
            int ret = 0;
            while ((ret = br.read(cbuf, off, len)) > 0) {
                off += ret;
                len -= ret;
            }
            bw.write(cbuf, 0, off);
            bw.flush();
        } finally {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
        }
    }
	
	public static void del(String filepath) throws IOException {
        File f = new File(filepath);// �����ļ�·��
        if (f.exists() && f.isDirectory()) {// �ж����ļ�����Ŀ¼
            if (f.listFiles().length == 0) {// ��Ŀ¼��û���ļ���ֱ��ɾ��
                f.delete();
            } else {// ��������ļ��Ž����飬���ж��Ƿ����¼�Ŀ¼
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// �ݹ����del������ȡ����Ŀ¼·��
                    }
                    delFile[j].delete();// ɾ���ļ�
                }
            }
        }
    }
}
