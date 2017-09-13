package capslock;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 *
 * @author RISCassembler
 */
enum LogHandler{
    instance;
    
    private FileHandler handler;
    
    private LogHandler() {
        final Logger logger = Logger.getLogger("testlogger");

        try {
            handler = new FileHandler("log.txt", true);
        } catch (IOException | SecurityException ex) {
            System.err.println(ex);
            System.exit(1);
        }
        
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }
    
    public final void close(){handler.close();}
}
