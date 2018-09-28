package com.nameless;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by boysz on 2018/9/27.
 */
public class BlockTest {

    @Ignore
    public void testCreateBlock(){

        //创世节点被创建了
        Block block = new Block(0,new Date(),"Hello World ! My Block !");
        //System.out.println(block.toString() + "\n" + block.hashBlock());

        Block block_Stanley = block.nextBlock("I'm block and my data is Stanley ");
        //System.out.println(block_Stanley.toString());
        //block_Stanley.append2block(block);

        Block block_Muller = block_Stanley.nextBlock("I'm block and my data is Muller ");
        //System.out.println(block_Muller.toString());
        //block_Muller.append2block(block_Stanley);

        Block block_Jack = block_Muller.nextBlock("I'm block and my data is Jack ");
        //System.out.println(block_Jack.toString());
        //block_Jack.append2block(block_Muller);

        print(block);
        print(block_Stanley);
        print(block_Muller);
        print(block_Jack);

    }

    @Test
    public void testCreateBlockWithNet(){

        Block block = new Block(0,new Date(),"Hello World ! My Block !");
        byte[] bytes = Block.obj2byte(block);

        /**
         * 上家用private key 加密 bytes , 下家用public key 解密bytes,
         * 每个节点都有自己的 private key 和 public key
         */
        Block block_Stanley = createBlock(bytes,"I'm block and my data is Stanley ");
        byte[] bytes_Stanley = Block.obj2byte(block_Stanley);

        Block block_Muller = createBlock(bytes_Stanley,"I'm block and my data is Muller ");
        byte[] bytes_Muller = Block.obj2byte(block_Muller);

        Block block_Jack = createBlock(bytes_Muller,"I'm block and my data is Jack ");
        byte[] byte_Jack = Block.obj2byte(block_Jack);

        print(block);
        print(block_Stanley);
        print(block_Muller);
        print(block_Jack);
    }

    /**
     * 这个方法会出现 java.lang.StackOverflowError 可以增加 -Xss<size>[g|G|m|M|k|K]
     * @param previousBytes
     * @param data
     * @return
     */
    public Block createBlock(byte[] previousBytes , String data){
        long s = System.currentTimeMillis();
        Block block = Block.byte2obj(previousBytes).nextBlock(data);
        while(!block.getHash().startsWith("0000")){
            block = createBlock(previousBytes,data);
        }
        long e = System.currentTimeMillis();
        System.out.println("e-s:"+(e-s));
        return block;
    }

    public void print(Block block){
        Iterator<Map.Entry<String,Block>> iter = block.getBlockChain().entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String,Block> entry = iter.next();
            System.out.println(String.format("key=%s,value=%s",entry.getKey(),entry.getValue()));
        }
        System.out.println();
    }


}
