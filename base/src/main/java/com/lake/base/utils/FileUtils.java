package com.lake.base.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * 文件操作工具类
 *
 * @author lake
 * create by 2021/8/9 3:40 下午
 */
public class FileUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * Rename the file.
     *
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * Rename the file.
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(final File file, final String newName) {
        // file is null then return false
        if (file == null) {
            return false;
        }
        // file doesn't exist then return false
        if (!file.exists()) {
            return false;
        }
        // the new name is space then return false
        if (isSpace(newName)) {
            return false;
        }
        // the new name equals old name then return true
        if (newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        // the new name of file exists then return false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    /**
     * Return whether it is a directory.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * Return whether it is a directory.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * Return whether it is a file.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * Return whether it is a file.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Copy the directory.
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyDir(final String srcDirPath,
                                  final String destDirPath) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    /**
     * Copy the directory.
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @param listener    The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyDir(final String srcDirPath,
                                  final String destDirPath,
                                  final OnReplaceListener listener) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * Copy the directory.
     *
     * @param srcDir  The source directory.
     * @param destDir The destination directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyDir(final File srcDir,
                                  final File destDir) {
        return copyOrMoveDir(srcDir, destDir, false);
    }

    /**
     * Copy the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyDir(final File srcDir,
                                  final File destDir,
                                  final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, false);
    }

    /**
     * Copy the file.
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFile(final String srcFilePath,
                                   final String destFilePath) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
    }

    /**
     * Copy the file.
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @param listener     The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFile(final String srcFilePath,
                                   final String destFilePath,
                                   final OnReplaceListener listener) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * Copy the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFile(final File srcFile,
                                   final File destFile) {
        return copyOrMoveFile(srcFile, destFile, false);
    }

    /**
     * Copy the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFile(final File srcFile,
                                   final File destFile,
                                   final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, false);
    }

    /**
     * Move the directory.
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveDir(final String srcDirPath,
                                  final String destDirPath) {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    /**
     * Move the directory.
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @param listener    The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveDir(final String srcDirPath,
                                  final String destDirPath,
                                  final OnReplaceListener listener) {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * Move the directory.
     *
     * @param srcDir  The source directory.
     * @param destDir The destination directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveDir(final File srcDir,
                                  final File destDir) {
        return copyOrMoveDir(srcDir, destDir, true);
    }

    /**
     * Move the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveDir(final File srcDir,
                                  final File destDir,
                                  final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, true);
    }

    /**
     * Move the file.
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveFile(final String srcFilePath,
                                   final String destFilePath) {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
    }

    /**
     * Move the file.
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @param listener     The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveFile(final String srcFilePath,
                                   final String destFilePath,
                                   final OnReplaceListener listener) {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * Move the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveFile(final File srcFile,
                                   final File destFile) {
        return copyOrMoveFile(srcFile, destFile, true);
    }

    /**
     * Move the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean moveFile(final File srcFile,
                                   final File destFile,
                                   final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, true);
    }

    private static boolean copyOrMoveDir(final File srcDir,
                                         final File destDir,
                                         final boolean isMove) {
        return copyOrMoveDir(srcDir, destDir, new OnReplaceListener() {
            @Override
            public boolean onReplace() {
                return true;
            }
        }, isMove);
    }

    private static boolean copyOrMoveDir(final File srcDir,
                                         final File destDir,
                                         final OnReplaceListener listener,
                                         final boolean isMove) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        // destDir's path locate in srcDir's path then return false
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) {
            return false;
        }
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        if (destDir.exists()) {
            if (listener == null || listener.onReplace()) {// require delete the old directory
                if (!deleteAllInDir(destDir)) {// unsuccessfully delete then return false
                    return false;
                }
            } else {
                return true;
            }
        }
        if (!createOrExistsDir(destDir)) {
            return false;
        }
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) {
                    return false;
                }
            } else if (file.isDirectory()) {
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) {
                    return false;
                }
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    private static boolean copyOrMoveFile(final File srcFile,
                                          final File destFile,
                                          final boolean isMove) {
        return copyOrMoveFile(srcFile, destFile, new OnReplaceListener() {
            @Override
            public boolean onReplace() {
                return true;
            }
        }, isMove);
    }

    private static boolean copyOrMoveFile(final File srcFile,
                                          final File destFile,
                                          final OnReplaceListener listener,
                                          final boolean isMove) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        // srcFile equals destFile then return false
        if (srcFile.equals(destFile)) {
            return false;
        }
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        if (destFile.exists()) {
            if (listener == null || listener.onReplace()) {// require delete the old file
                if (!destFile.delete()) {// unsuccessfully delete then return false
                    return false;
                }
            } else {
                return true;
            }
        }
        if (!createOrExistsDir(destFile.getParentFile())) {
            return false;
        }
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile))
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete the directory.
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean delete(final String filePath) {
        return delete(getFileByPath(filePath));
    }

    /**
     * Delete the directory.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean delete(final File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            return deleteDir(file);
        }
        return deleteFile(file);
    }

    /**
     * Delete the directory.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteDir(final String dirPath) {
        return deleteDir(getFileByPath(dirPath));
    }

    /**
     * Delete the directory.
     *
     * @param dir The directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteDir(final File dir) {
        if (dir == null) {
            return false;
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true;
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * Delete the file.
     *
     * @param srcFilePath The path of source file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFile(final String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * Delete the file.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * Delete the all in directory.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteAllInDir(final String dirPath) {
        return deleteAllInDir(getFileByPath(dirPath));
    }

    /**
     * Delete the all in directory.
     *
     * @param dir The directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteAllInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    /**
     * Delete all files in directory.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDir(final String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    /**
     * Delete all files in directory.
     *
     * @param dir The directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDirWithFilter(final String dirPath,
                                                     final FileFilter filter) {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean deleteFilesInDirWithFilter(final File dir, final FileFilter filter) {
        if (dir == null) {
            return false;
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true;
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) {
                            return false;
                        }
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Return the files in directory.
     * <p>Doesn't traverse subdirectories</p>
     *
     * @param dirPath The path of directory.
     * @return the files in directory
     */
    public static List<File> listFilesInDir(final String dirPath) {
        return listFilesInDir(dirPath, false);
    }

    /**
     * Return the files in directory.
     * <p>Doesn't traverse subdirectories</p>
     *
     * @param dir The directory.
     * @return the files in directory
     */
    public static List<File> listFilesInDir(final File dir) {
        return listFilesInDir(dir, false);
    }

    /**
     * Return the files in directory.
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    public static List<File> listFilesInDir(final String dirPath, final boolean isRecursive) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive);
    }

    /**
     * Return the files in directory.
     *
     * @param dir         The directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    public static List<File> listFilesInDir(final File dir, final boolean isRecursive) {
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        }, isRecursive);
    }

    /**
     * Return the files that satisfy the filter in directory.
     * <p>Doesn't traverse subdirectories</p>
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false);
    }

    /**
     * Return the files that satisfy the filter in directory.
     * <p>Doesn't traverse subdirectories</p>
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(dir, filter, false);
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive);
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        if (!isDir(dir)) {
            return null;
        }
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    //noinspection ConstantConditions
                    list.addAll(listFilesInDirWithFilter(file, filter, true));
                }
            }
        }
        return list;
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param filePath The path of file.
     * @return the time that the file was last modified
     */

    public static long getFileLastModified(final String filePath) {
        return getFileLastModified(getFileByPath(filePath));
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param file The file.
     * @return the time that the file was last modified
     */
    public static long getFileLastModified(final File file) {
        if (file == null) {
            return -1;
        }
        return file.lastModified();
    }

    /**
     * Return the charset of file simply.
     *
     * @param filePath The path of file.
     * @return the charset of file simply
     */
    public static String getFileCharsetSimple(final String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    /**
     * Return the charset of file simply.
     *
     * @param file The file.
     * @return the charset of file simply
     */
    public static String getFileCharsetSimple(final File file) {
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**
     * Return the number of lines of file.
     *
     * @param filePath The path of file.
     * @return the number of lines of file
     */
    public static int getFileLines(final String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /**
     * Return the number of lines of file.
     *
     * @param file The file.
     * @return the number of lines of file
     */
    public static int getFileLines(final File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readChars;
            if (LINE_SEP.endsWith("\n")) {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\n') {
                            ++count;
                        }
                    }
                }
            } else {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\r') {
                            ++count;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * Return the size of directory.
     *
     * @param dirPath The path of directory.
     * @return the size of directory
     */
    public static String getDirSize(final String dirPath) {
        return getDirSize(getFileByPath(dirPath));
    }

    /**
     * Return the size of directory.
     *
     * @param dir The directory.
     * @return the size of directory
     */
    public static String getDirSize(final File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * Return the length of file.
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    public static String getFileSize(final String filePath) {
        long len = getFileLength(filePath);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    public static String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * Return the length of directory.
     *
     * @param dirPath The path of directory.
     * @return the length of directory
     */
    public static long getDirLength(final String dirPath) {
        return getDirLength(getFileByPath(dirPath));
    }

    /**
     * Return the length of directory.
     *
     * @param dir The directory.
     * @return the length of directory
     */
    public static long getDirLength(final File dir) {
        if (!isDir(dir)) {
            return -1;
        }
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    /**
     * Return the length of file.
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    public static long getFileLength(final String filePath) {
        boolean isURL = filePath.matches("[a-zA-z]+://[^\\s]*");
        if (isURL) {
            try {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(filePath).openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    return conn.getContentLength();
                }
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileLength(getFileByPath(filePath));
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) {
            return -1;
        }
        return file.length();
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    public static String getFileMD5ToString(final String filePath) {
        File file = isSpace(filePath) ? null : new File(filePath);
        return getFileMD5ToString(file);
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    public static String getFileMD5ToString(final File file) {
        return bytes2HexString(getFileMD5(file));
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    public static byte[] getFileMD5(final String filePath) {
        return getFileMD5(getFileByPath(filePath));
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    public static byte[] getFileMD5(final File file) {
        if (file == null) {
            return null;
        }
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) {
                    break;
                }
            }
            md = dis.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Return the file's path of directory.
     *
     * @param file The file.
     * @return the file's path of directory
     */
    public static String getDirName(final File file) {
        if (file == null) {
            return "";
        }
        return getDirName(file.getAbsolutePath());
    }

    /**
     * Return the file's path of directory.
     *
     * @param filePath The path of file.
     * @return the file's path of directory
     */
    public static String getDirName(final String filePath) {
        if (isSpace(filePath)) {
            return "";
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    /**
     * Return the name of file.
     *
     * @param file The file.
     * @return the name of file
     */
    public static String getFileName(final File file) {
        if (file == null) {
            return "";
        }
        return getFileName(file.getAbsolutePath());
    }

    /**
     * Return the name of file.
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    public static String getFileName(final String filePath) {
        if (isSpace(filePath)) {
            return "";
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * Return the name of file without extension.
     *
     * @param file The file.
     * @return the name of file without extension
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) {
            return "";
        }
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * Return the name of file without extension.
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * Return the extension of file.
     *
     * @param file The file.
     * @return the extension of file
     */
    public static String getFileExtension(final File file) {
        if (file == null) {
            return "";
        }
        return getFileExtension(file.getPath());
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(lastPoi + 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public interface OnReplaceListener {
        boolean onReplace();
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        int len = bytes.length;
        if (len <= 0) {
            return "";
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.3fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.3fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.3fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.3fGB", (double) byteNum / 1073741824);
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean writeFileFromIS(final File file,
                                           final InputStream is) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[8192];
            int len;
            while ((len = is.read(data, 0, 8192)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据URI获取文件真实路径（兼容多张机型）
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {

            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= 19) { // api >= 19
                return getRealPathFromUriAboveApi19(context, uri);
            } else { // api < 19
                return getRealPathFromUriBelowAPI19(context, uri);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String type = documentId.split(":")[0];
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};

                //
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                filePath = getDataColumn(context, contentUri, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            } else if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else {
                //Log.e("路径错误");
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 复制assets下的文件到本地路径
     */
    public static void copyAssetsFileToPath(Context context, String filename, String path) {
        AssetManager assetManager = context.getAssets();
        long start = System.currentTimeMillis();
        Log.e("lake", "copyAssetsFileToPath: " + start);
        try {
            File outF = new File(path);
            if (outF.exists()) {
                outF.delete();
            }
            outF.createNewFile();
            InputStream is = assetManager.open(filename);
            FileOutputStream out = new FileOutputStream(outF);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                out.write(buffer, 0, byteCount);
            }
            out.flush();
            is.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加内容进入文件
     *
     * @param fileName 文件名称
     * @param content  内容
     * @param path     路径
     * @return
     */
    public static boolean addContentToFile(String fileName, String content, String path) {
        boolean isSuccess = false;
        BufferedWriter bufferedWriter = null;
        try {
            //创建文件夹
            File dirs = new File(path);
            if (!dirs.exists()) {
                dirs.mkdirs();
            }

            //文件不存在就创建
            File file = new File(path + "/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            boolean isAdd = true;
            if (file.length() > 20 * 1024 * 1024) {
                //大于20mb更新文件
                isAdd = false;
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, isAdd)));
            bufferedWriter.write(content + "\r\n");
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }
}
