

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.ListFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AmazonRekognitionClient amazonRekognitionClient;
    int image_index = R.drawable.your_Image;
    Image image = new Image();
    String CollectionID = "your_AWS_CollectionID";
    String ExternalID = "your_Image_ExternalID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCredential();
        new AsyncTaskRunner().execute();
    }

    public void getCredential(){
        amazonRekognitionClient = new AmazonRekognitionClient(new AWSCredentialProvider());
        amazonRekognitionClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));    //Put Your AWS Region here
    }

    public String ListCollections(){
        ListCollectionsRequest request = new ListCollectionsRequest();
        ListCollectionsResult response = amazonRekognitionClient.listCollections(request);
        return response.toString();
    }

    public String CreateCollection(){
        CreateCollectionRequest request = new CreateCollectionRequest().withCollectionId(CollectionID);
        CreateCollectionResult response = amazonRekognitionClient.createCollection(request);
        String statusCode = response.getStatusCode().toString();
        return "statusCode: "+statusCode+": "+response.toString();
    }

    public String DeleteCollection(){
        DeleteCollectionRequest request = new DeleteCollectionRequest().withCollectionId(CollectionID);
        DeleteCollectionResult response = amazonRekognitionClient.deleteCollection(request);
        String statusCode = response.getStatusCode().toString();
        return "statusCode: "+statusCode+": "+response.toString();
    }

    public String IndexFaces(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image_index);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        ByteBuffer imageBytes = ByteBuffer.wrap(stream.toByteArray());
        image.withBytes(imageBytes);

        IndexFacesRequest request = new IndexFacesRequest()
                .withCollectionId(CollectionID)
                .withImage(image)
                .withExternalImageId(ExternalID)
                .withDetectionAttributes("ALL");
        IndexFacesResult response = amazonRekognitionClient.indexFaces(request);
        List faceRecords = response.getFaceRecords();
        return "faceRecords: "+ faceRecords +" response: "+response.toString();
    }

    public String ListFaces(){
        ListFacesRequest request = new ListFacesRequest()
             .withCollectionId(CollectionID)
             .withMaxResults(100);
        ListFacesResult response = amazonRekognitionClient.listFaces(request);
        List faceList = response.getFaces();
        return "faceList: " + faceList+ " response: "+response;
    }

    public String SearchFacesByImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image_index);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        ByteBuffer imageBytes = ByteBuffer.wrap(stream.toByteArray());
            image.withBytes(imageBytes);

        SearchFacesByImageRequest request = new SearchFacesByImageRequest()
                .withCollectionId(CollectionID)
                .withImage(image);
        SearchFacesByImageResult response = amazonRekognitionClient.searchFacesByImage(request);

    
        try {
            JSONObject responseObject = new JSONObject(response.toString());
            ExternalID = "";
            //for those very similar faces
            for(int index =0; index < responseObject.getJSONArray("FaceMatches").length(); index++){
                JSONObject faceMatches = new JSONObject(responseObject.getJSONArray("FaceMatches").get(index).toString());

                String similarity = faceMatches.getString("Similarity");
                String externalImageId = faceMatches.getJSONObject("Face").getString("ExternalImageId");
                String confidence = faceMatches.getJSONObject("Face").getString("Confidence");
                Log.e("0","similarity"+similarity);
                Log.e("0","externalImageId"+externalImageId);
                Log.e("0","confidence"+confidence);
                ExternalID = ExternalID+externalImageId+" ";
            }


        } catch (JSONException e) {
            Log.e("", "unexpected JSON exception: ", e);
        }


        return "ExternalID: "+ExternalID+ " response: "+response;
    }


    private class AsyncTaskRunner extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = IndexFaces();
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w("0","Result: "+result);
            Toast.makeText(getApplicationContext(),"Result: "+result, Toast.LENGTH_LONG).show();
        }
    }
}
