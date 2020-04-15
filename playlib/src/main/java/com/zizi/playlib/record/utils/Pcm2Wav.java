package com.zizi.playlib.record.utils;

import android.media.AudioFormat;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Pcm2Wav
{

    public void convertAudioFiles(String src, String target,int sampleRateInHz, int channelConfig, int audioFormat) throws Exception
    {
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(target);


        byte[] buf = new byte[1024 * 1000];
        int size = fis.read(buf);
        int PCMSize = 0;
        while (size != -1)
        {
            PCMSize += size;
            size = fis.read(buf);
        }
        fis.close();


        WaveHeader header = new WaveHeader();
        header.fileLength = PCMSize + (44 - 8);
        header.FmtHdrLeth = 16;
        if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
            header.BitsPerSample = 16;
        } else {
            header.BitsPerSample = 8;
        }
        if (channelConfig == AudioFormat.CHANNEL_OUT_STEREO) {
            header.Channels = 2;
        } else {
            header.Channels = 1;
        }
        header.FormatTag = 0x0001;
        header.SamplesPerSec = sampleRateInHz;
        header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
        header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
        header.DataHdrLeth = PCMSize;

        byte[] h = header.getHeader();

        assert h.length == 44;
        //write header
        fos.write(h, 0, h.length);
        //write data stream
        fis = new FileInputStream(src);
        size = fis.read(buf);
        while (size != -1)
        {
            fos.write(buf, 0, size);
            size = fis.read(buf);
        }
        fis.close();
        fos.close();
//        System.out.println(System.currentTimeMillis()+"wav");
//        System.out.println("Convert OK!");
    }
}
