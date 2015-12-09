package com.awesome.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.redis.metadata.RedisMetadataStore;
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Configuration
public class AwesomeSftpApplicationConfiguration {

    @Bean
    public DefaultSftpSessionFactory ftpClientFactory(
        @Value("username") String username,
        @Value("password") String password,
        @Value("my_ftp_server_host") String host,
        @Value("22") int port
    ) {
        System.out.println("LOOK AT ME -----------------------------------------------------");
        System.out.println("ftpClientFactory");
        System.out.println("LOOK AT ME -----------------------------------------------------");

        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setUser(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        factory.setAllowUnknownKeys(true);

        return factory;
    }

    @Bean
    public QueueChannel sftpChannel() {
        System.out.println("LOOK AT ME -----------------------------------------------------");
        System.out.println("sftpChannel");
        System.out.println("LOOK AT ME -----------------------------------------------------");

        QueueChannel queueChannel = new QueueChannel();
        queueChannel.addInterceptor(new WireTap(new MessageChannel() {
            @Override
            public boolean send(Message<?> message) {
                return false;
            }

            @Override
            public boolean send(Message<?> message, long timeout) {
                String content = null;
                try {
                    content = new Scanner((File) message.getPayload()).useDelimiter("\\Z").next();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("LOOK AT ME -----------------------------------------------------");
                System.out.println(content);
                System.out.println("LOOK AT ME -----------------------------------------------------");
                return false;
            }
        }));
        return queueChannel;
    }

    @Bean
    @InboundChannelAdapter(value = "sftpChannel", poller = @Poller(fixedRate = "100000"))
    public SftpInboundFileSynchronizingMessageSource inboundSftpChannelAdapter(
        SessionFactory<ChannelSftp.LsEntry> factory,
        CompositeFileListFilter<ChannelSftp.LsEntry> compositeFileListFilter,
        @Value("my_remote_directory") String remoteDir,
        @Value("my_local_directory") String localDir
    ) {
        System.out.println("LOOK AT ME -----------------------------------------------------");
        System.out.println("inboundSftpChannelAdapter");
        System.out.println("LOOK AT ME -----------------------------------------------------");
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(factory);

        SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(synchronizer);
        source.setLocalDirectory(new File(localDir));
        synchronizer.setFilter(compositeFileListFilter);
        synchronizer.setRemoteDirectory(remoteDir);

        return source;
    }

    @Bean
    public CompositeFileListFilter<ChannelSftp.LsEntry> compositeFileListFilter(
        ConcurrentMetadataStore concurrentMetadataStore,
        @Value("my_file_name.txt") String fileFilter
    ) {
        CompositeFileListFilter<ChannelSftp.LsEntry> compositeFileListFilter = new CompositeFileListFilter<>();
        compositeFileListFilter.addFilter(new SftpPersistentAcceptOnceFileListFilter(concurrentMetadataStore, "ftp file "));
        compositeFileListFilter.addFilter(new SftpSimplePatternFileListFilter(fileFilter));

        return compositeFileListFilter;
    }

    @Bean
    public ConcurrentMetadataStore concurrentMetadataStore(RedisConnectionFactory connectionFactory) {
        return new RedisMetadataStore(connectionFactory);
    }

    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory();
    }
}
