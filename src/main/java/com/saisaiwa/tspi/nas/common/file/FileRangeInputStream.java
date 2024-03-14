package com.saisaiwa.tspi.nas.common.file;

import com.saisaiwa.tspi.nas.common.exception.FileObjectNotFound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * @description:
 * @date: 2024/03/14 17:06
 * @author: saisiawa
 **/
public class FileRangeInputStream extends InputStream {
    private final RandomAccessFile file;
    private final long end;
    private long position;

    public FileRangeInputStream(File f, long start, long end) throws IOException {
        if (f.isDirectory() || !f.exists()) {
            throw new FileObjectNotFound();
        }
        this.file = new RandomAccessFile(f, "r");
        this.end = end;
        this.position = start;
        file.seek(start);
    }

    @Override
    public int read() throws IOException {
        if (position > end) {
            return -1;
        }
        position++;
        return file.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (position > end) {
            return -1;
        }
        int bytesToRead = (int) Math.min(len, end - position + 1);
        int bytesRead = file.read(b, off, bytesToRead);
        position += bytesRead;
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
