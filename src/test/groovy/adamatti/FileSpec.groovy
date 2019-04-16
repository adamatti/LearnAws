package adamatti

import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import spock.lang.Specification

/**
 * https://docs.aws.amazon.com/pt_br/sdk-for-java/v2/developer-guide/s3-examples.html
 */
class FileSpec extends Specification {
    private static final Region region = Region.US_EAST_1

    private static final S3Client s3 = S3Client.builder()
        .region(region)
        .endpointOverride("http://localhost:4572".toURI())
        .credentialsProvider(TestCredentialsFactory.build())
        .build()

    def cleanupSpec(){
        s3?.close()
    }

    def "file operations"(){
        given:
            def bucketName = "sample-bucket"
        when:
            createBucket(bucketName)
            listBuckets()
            upload(bucketName,"key")
            download(bucketName,"key")
        then:
            noExceptionThrown()
    }

    private void download(String bucketName, String key){
        def request = GetObjectRequest.builder().bucket(bucketName).key(key).build()

        ResponseTransformer transformer = ResponseTransformer.toBytes()

        ResponseBytes response = s3.getObject(request, transformer)
        println "Download response: ${response}"

        println "Download string: ${new String(response.asByteArray())}"
    }

    private void upload(String bucketName, String key){
        S3Request request = PutObjectRequest.builder().bucket(bucketName).key(key).build()

        RequestBody body = RequestBody.fromString("sample")

        def response = s3.putObject(request,body)

        println "Upload response: ${response}"
    }

    private void createBucket(String bucketName){
        def config = CreateBucketConfiguration.builder()
            .locationConstraint(region.id())
            .build()

        CreateBucketRequest createBucketRequest = CreateBucketRequest
            .builder()
            .bucket(bucketName)
            .createBucketConfiguration(config)
            .build()

        def response = s3.createBucket(createBucketRequest)
        println "S3 create response: ${response}"
    }

    private void listBuckets(){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build()

        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest)

        listBucketsResponse.buckets().each { Bucket bucket ->
            println "Bucket list: ${bucket}"
        }
    }
}
