package mwdindustries.aria.util;
import java.lang.Math;
import java.util.Vector;
//Begin Andy Mcnutt's data values and structures for manipulation of viewing angle and stored gps data

public class GPSPOI {
    //used for identification of building point, will be displayed to the user
    public String Name;
    //slightly more fancy description with more details, like college of engineering, mathematics, ect
    public String DisplayName;
    //full description to be used for in depth description, aim to be provided by tour guides and give more deteails
    public String Description;
    //center point for radius detection,
    // will be only point for points of interest like statues and other points, buildings will have an extended list.
    //All longitude / Latitude coordinates will be stored in decimal format, so minutes and seconds will need to be converted
    public double Latcenter;
    public double Longcenter;
    //used to filter out the distance that the POI is relevant, object specific
    //unit measurements will be in feet
    public double radius;
    //used instead of child hierarchy to determine if this point is a building, art, or other given point
/*
 1 = academic building
 2 = resedential building
 3 = adminstrative / maintenance building
 4 = sculpture / landmark / landscaping
 5 = parking lots

 will be automaticlly assigned based on the order of file reading,
  can ignore this if we utilize hierarchy correctly
  used to filter what to display if user selects it from the list
*/
    public int type;
    /*
    building specific array of points, or reserved for large areas such as parking lots
    point out corners of buildings and determine viewing ranges of to display onto the screen,
    handler will handle overlapping with other objects, this will just return the max, min veiwing
    angles of that building and the distance to those specific points.
    */
    private int numPoints;
    Vector<Double> latPoints;
    Vector<Double> longPoints;

    /*
    holds the max and min angle of the object relative to the user,
    will need to update with void function before checking each time
     */
    public double maximumAngle = 360;
    private int maxIndex = 0;
    public double minimumAngle = 0;
    int minIndex = 0;
    public GPSPOI(){
        Name = "";
        DisplayName = "";
        Description = "";
        Latcenter = 0;
        Longcenter = 0;
        radius = 500;
        type = 0;
        numPoints = 0;
        latPoints = new Vector<Double>();
        longPoints = new Vector<Double>();
    }
    //in depth constructor made by the file reader with all known data to build a data structure immediatly
    public GPSPOI(String N, String Display, String Descrip,double Lat, double Long, double rad, int t, int vecsize){
        Name = N;
        DisplayName = Display;
        Description = Descrip;
        Latcenter = Lat;
        Longcenter = Long;
        radius = rad;
        type = t;
        numPoints = vecsize;
        latPoints = new Vector<Double>(vecsize);
        longPoints = new Vector<Double>(vecsize);
    }
    //while reading the file, this will repeatedly be called to add a corner point
    //index will be accumulated each time, probably will be the current i in the for loop
    public void addCordinate(double nLat, double nLong, int index) {
        latPoints.add(index, nLat);
        longPoints.add(index, nLong);
    }
    //basic get function to obtain the compass bearing to view the POI from the user's location
    //will be returned in range of (0 - 360 degrees)
    //increases clockwise as it moves from North direction
    double calcAngle(double uLat, double uLong){
        double dLat = Latcenter - uLat;
        double dLong = Longcenter - uLong;
        //standardize the unit of distance for calcualtion
        dLat = dLat * 364574;
        dLong = dLong * 280177;
        double angle = Math.atan2(dLat,dLong);
        //convert angle from ranges (-PI to PI) to degrees bearing (0 to 360)
        angle = angle / Math.PI * 180;
        if (angle < 0) {
            angle = angle + 360;
        }
        return angle;
    }
    //used with the vector points, for individual points, returns 0-360 degrees as well.
    double calcAngle(double uLat, double uLong, int index){
        double dLat = latPoints.get(index) - uLat;
        double dLong = longPoints.get(index) - uLong;
        //convert into feet for a standardized unit of distance for angle calculation
        dLat = dLat * 364574;
        dLong = dLong * 280177;
        double angle = Math.atan2(dLat,dLong);

        //convert angle from ranges (-PI to PI) to degrees bearing (0 to 360)
        angle = angle / Math.PI * 180;
        if (angle < 0) {
            angle = angle + 360;
        }
        return angle;
    }
    /*used to calculate if user in in the object's range of display
   to determine if it is reasonable to display the point of interest so that it doesn't just clutter
   the screen
   converts the
   */
    boolean withinRange(double uLat, double uLong, double scale) {
        double dLat = Latcenter - uLat;
        double dLong = Longcenter - uLong;
        //convert coordinate distances to feet
        //conversions obtained from csgnetwrok.com with inputs derived from sample data
        dLat = dLat * 364574;
        // dlong conversions = dlat conversion * cos (39.78) which is the latitude of the campus
        dLong = dLong * 280177;
        double distance = Math.sqrt((dLat * dLat)+(dLong * dLong));
        boolean inRange = distance <= (radius * scale);
        return inRange;
    }
    //indicates the index of the point that
    void clacViewAngles(double uLat, double uLong){
        /*checks to see if there are points present in 1st quadrant (0 - 90 dgrees) and
        2nd quadrant (270 - 360) at same time before redoing all that and picking out the maximum angle

        */
        boolean fQuad = false;
        boolean sQuad = false;
       for(int i = 0; i < numPoints; i++){
          double angle = calcAngle(uLat, uLong, i);
           if (angle < 0 && angle > 270) {
               sQuad = true;
           }
           if (angle < 90 && angle > 0) {
               fQuad = true;
           }
           //exit out of for loop.
           // optional keep in here if # of points is too long or this will slow down the check for small number of points
           if(fQuad && sQuad){
               i = numPoints;
           }

       }
        int degreeTrun = 0;
        //adds 360 to the angle if in 1st and 2nd quadrant for comparison, others operates normally.
        if (fQuad && sQuad) {
          degreeTrun = 360;
        }
        /*now calculates and updates the minimum and maximum viewing angles of the object
        will need to be called each time before you grab the values through the get functions.


*/
        //resets angle to base values, these will have to change
        maximumAngle = 360;
        minimumAngle = 0;
        for(int i = 0; i < numPoints; i++){
            double angle = calcAngle(uLat, uLong, i);
            if (angle < 180){
                angle = angle + degreeTrun;
            }
            if(angle > maximumAngle){
                maximumAngle = angle;
                maxIndex = i;
            }
            if(angle  < minimumAngle + degreeTrun){
                maximumAngle = angle ;
                maxIndex = i;
            }
        }
        /*note this will store the maximum angle as 90 - 450 in the case that there are
         points in both the 1st and 2nd quadrant (NE and NW quadrants)* so will need to rectify this
         before leaving*/
        if (maximumAngle > 360)
            maximumAngle -=360;

    }
}

