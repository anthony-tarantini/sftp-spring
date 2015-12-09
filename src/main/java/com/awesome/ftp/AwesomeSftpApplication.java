package com.awesome.ftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class AwesomeSftpApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(AwesomeSftpApplication.class, args);

        System.out.println("LOOK AT ME -----------------------------------------------------");
        System.out.println("SPRING STARTED");
        System.out.println("LOOK AT ME -----------------------------------------------------");
    }
}
