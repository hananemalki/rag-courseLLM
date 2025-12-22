package org.mql.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class CourseLlmApplication {

    private static final Logger logger = LoggerFactory.getLogger(CourseLlmApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(CourseLlmApplication.class, args);
        
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        
        logger.info("""
            
             CourseLLM Application démarrée! 
            
             URLs d'accès:
               Local:    http://localhost:{}
               Network:  http://{}:{}
            
             API Endpoints:
               Ask:      POST http://localhost:{}/api/chat/ask
               Upload:   POST http://localhost:{}/api/documents/upload
               Index:    POST http://localhost:{}/api/documents/index-all
            
             Configuration:
               Ollama:   {}
               ChromaDB: {}
               Courses:  {}
            
             Prêt à répondre aux questions!
            """,
            port,
            hostAddress, port,
            port,
            port,
            port,
            port,
            env.getProperty("ollama.base-url"),
            env.getProperty("chroma.base-url"),
            env.getProperty("rag.courses-directory")
        );
    }
}
