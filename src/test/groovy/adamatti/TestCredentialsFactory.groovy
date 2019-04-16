package adamatti

import groovy.transform.CompileStatic
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider

@CompileStatic
abstract class TestCredentialsFactory {
    static AwsCredentialsProvider build(){
        StaticCredentialsProvider.create(
            AwsBasicCredentials.create("test","test")
        )
    }
}
