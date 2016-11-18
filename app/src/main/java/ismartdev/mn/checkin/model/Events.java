package ismartdev.mn.checkin.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class Events {
    public String start_date="" ;
    public String name ="";
    public String description="";
    public String expire_date="";
    public String image;
    public String place_image;
    public boolean online;

    public Events() {

    }


}
