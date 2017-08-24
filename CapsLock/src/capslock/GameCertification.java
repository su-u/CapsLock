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
    private final String description;
    private final Path ExecutablePath;
    private final String version;
    private final Path PanelPath;
    private final List<Path> ImagesPathList;
    private final List<Path> MoviesPathList;
    
    public GameCertification(JSONObject record){
        uuid = UUID.fromString(record.getString("UUID"));
        name = record.getString("name");
        description = record.getString("description");
        ExecutablePath = new File(record.getString("executable")).toPath();
        version = record.getString("version");
        PanelPath = new File(record.getString("panel")).toPath();
        ImagesPathList = BuildImmutableArray(record.getJSONArray("image"));
        MoviesPathList = BuildImmutableArray(record.getJSONArray("movie"));
    }
    
    public UUID getUUID(){return uuid;}
    public String getName(){return name;}
    public String getDescription(){return description;}
    public Path getExecutablePath(){return ExecutablePath;}
    public String getVersion(){return version;}
    public Path getPanelPath(){return PanelPath;}
    public List<Path> getImagesPathList(){return ImagesPathList;}
    public List<Path> getMoviePathList(){return MoviesPathList;}
    
    public void dump(){
        System.out.println(uuid.toString());
        System.out.println(name);
        System.out.println(description);
        System.out.println(ExecutablePath.toString());
        System.out.println(version);
        System.out.println(PanelPath);
        System.out.println(ImagesPathList);
        System.out.println(MoviesPathList);
    }
    
    private List<Path> BuildImmutableArray(JSONArray DataArray){
        ArrayList<Path> Builder = new ArrayList();
        DataArray.forEach(file -> Builder.add(new File(file.toString()).toPath()));
        return Collections.unmodifiableList(Builder);
    }
}
