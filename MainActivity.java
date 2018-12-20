

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
    int image_index = R.drawable.old;
    Image image = new Image();
    String CollectionID = "";
    String ExternalID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCredential();
        new AsyncTaskRunner().execute();
    }

    public void getCredential(){
        amazonRekognitionClient = new AmazonRekognitionClient(new AWSCredentialProvider());
        amazonRekognitionClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
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
        String ExternalID = response.toString();
        return "ExternalID: "+ExternalID;
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
