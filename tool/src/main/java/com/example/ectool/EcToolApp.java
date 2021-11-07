package com.example.ectool;

import com.example.ectool.handler.AssembleHandler;
import com.example.ectool.handler.DisassembleHandler;
import com.example.ectool.handler.Handler;
import com.example.ectool.utils.StringUtils;
import java.util.Scanner;

public class EcToolApp {
    
    public static void main(String[] args) throws Exception {
        String arg;
        Handler handler;
        if (args != null && args.length > 0 && StringUtils.isNotBlank((arg = args[0])) && (handler = getHandler(arg)) != null) {
            try {
                handler.handle();
            } catch (Exception ex) {
                System.out.println("执行异常," + ex.getMessage());
            }
        } else {
            System.out.println("参数错误(assemble/disassemble)");
        }
        System.out.print("执行完毕,按Enter结束");
        new Scanner(System.in).nextLine();
    }
    
    private static Handler getHandler(String arg) {
        switch (arg) {
            case "assemble":
                return new AssembleHandler();
            case "disassemble":
                return new DisassembleHandler();
            default:
                return null;
        }
    }
}
