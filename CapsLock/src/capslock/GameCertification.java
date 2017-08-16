package capslock;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author RISCassembler
 */

public final class GameCertification {
    private final UUID uuid;
    private final String name;
    private final Path ExecutablePath;
    private final String version;
    private final List<Path> ImagesPathList;
    private final List<Path> MoviesPathList;
    
    public GameCertification(JSONObject record){
        uuid = UUID.fromString(record.getString("UUID"));
        name = record.getString("name");
        ExecutablePath = new File(record.getString("executable")).toPath();
        version = record.getString("version");
        ImagesPathList = BuildImmutableArray(record.getJSONArray("image"));
        MoviesPathList = BuildImmutableArray(record.getJSONArray("movie"));
    }
    
    public Path getExecutablePath(){
        return ExecutablePath;
    }
    
    public List<Path> getImagesPathList(){
        return ImagesPathList;
    }
    
    public void dump(){
        System.out.println(uuid.toString());
        System.out.println(name);
        System.out.println(ExecutablePath.toString());
        System.out.println(version);
        System.out.println(ImagesPathList);
        System.out.println(MoviesPathList);
    }
    
    private List<Path> BuildImmutableArray(JSONArray DataArray){
        ArrayList<Path> Builder = new ArrayList();
        DataArray.forEach(file -> Builder.add(new File(file.toString()).toPath()));
        return Collections.unmodifiableList(Builder);
    }
}
