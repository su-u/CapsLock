package capslock;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RISCassembler
 */
enum LogHandler{
    instance;
    
    private FileHandler handler;
    private final Logger logger;
    
    private LogHandler() {
        logger = Logger.getLogger("DefaultLogger");

        try {
            handler = new FileHandler("log.txt", true);
        } catch (IOException | SecurityException ex) {
            System.err.println(ex);
            System.exit(1);
        }
        
        handler.setFormatter(new LogFormatter());
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
    }
    
    public final void close(){handler.close();}
    public final void severe(String msg){logger.severe(msg);}
    public final void warning(String msg){logger.warning(msg);}
    public final void info(String msg){logger.info(msg);}
    public final void config(String msg){logger.config(msg);}
    public final void fine(String msg){logger.fine(msg);}
    public final void finer(String msg){logger.finer(msg);}
    public final void finest(String msg){logger.finest(msg);}
}
