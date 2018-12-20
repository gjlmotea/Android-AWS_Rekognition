import com.amazonaws.auth.AWSCredentials;

public class AWSCredentialProvider implements AWSCredentials {

    @Override
    public String getAWSAccessKeyId() {
        return "put your Access Key here";
    }

    @Override
    public String getAWSSecretKey() {
        return "put your Secert Key here";
    }

}
