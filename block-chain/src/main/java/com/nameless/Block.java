package com.nameless;

import lombok.Data;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author stanley
 * Created by boysz on 2018/9/27.
 */
@Data
public class Block implements Serializable{

    private LinkedHashMap<String,Block> blockChain = new LinkedHashMap<>();
    private long index ;
    private Date time ;
    private Object data ;
    private String hash ;
    private String previousHash ;
    private double nonce;

    /**
     * genesis block
     * @param index
     * @param time
     * @param data
     */
    public Block(long index, Date time , Object data) {
        this.previousHash = str2sha256("I'm root block !");
        this.previousHash = hashBlock();
        this.initialize(index,time,data,this.previousHash);
    }

    /**
     * @param nextBlockData 区块链中将要追加Block中的数据
     * @return
     */
    public Block nextBlock(Object nextBlockData) {
        long nextIndex = this.index + 1;
        Date nextTime = new Date();
        Object nextData = nextBlockData;
        String nextPreviousHash = this.hashBlock();
        return new Block(nextIndex,nextTime,nextData,nextPreviousHash).append2block(this);
    }

    /**
     *
     * @param block
     * @return
     */
    public static byte[] obj2byte(Block block){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(block);
            oos.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static Block byte2obj(byte[] bytes){
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Block)ois.readObject();
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将创建的BLOCK追加到前一个节点的链中
     * @param previousBlock
     * @return 返回当前的Block
     */
    private Block append2block(Block previousBlock) {
        //上一个节点的链传递到当前BLOCK中
        this.getBlockChain().putAll(previousBlock.getBlockChain());
        return this;
    }

    /**
     * 将本对象hash
     * @return
     */
    private String hashBlock() {
        return this.hash = str2sha256(this.toString());
    }

    /**
     *
     * @param index
     * @param time
     * @param data
     * @param previousHash
     */
    private Block(long index, Date time , Object data, String previousHash) {
        this.initialize(index,time,data,previousHash);
    }

    /**
     *
     * @param index
     * @param time
     * @param data
     * @param previousHash
     */
    private void initialize(long index, Date time , Object data, String previousHash) {
        this.index = index ;
        this.time = time ;
        this.data = data ;
        this.previousHash = previousHash;
        this.nonce = Math.random();
        this.getBlockChain().put(this.hashBlock(),this);
    }

    /**
     * hash
     * @param str
     * @return
     */
    private String str2sha256(String str) {
        MessageDigest messageDigest = null;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encodeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", time=" + time +
                ", data=" + data +
                ", hash=" + hash +
                ", previousHash='" + previousHash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}
