package adamatti


import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.*
import spock.lang.Specification

/**
 * https://docs.aws.amazon.com/pt_br/sdk-for-java/v2/developer-guide/sqs-examples.html
 */
class QueueSpec extends Specification {
    private static SqsClient sqsClient

    def setupSpec(){

        sqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride("http://localhost:4576".toURI())
            .credentialsProvider(TestCredentialsFactory.build())
            .build()
    }


    def cleanupSpec(){
        sqsClient?.close()
    }

    def "list queue"(){
        given:
            def queueName = "sampleQueue"
        when:
            createQueue(queueName)
            listQueues()
            send_a_message(queueName)
            readMessages(queueName)
        then:
            1 == 1
    }

    private void readMessages(String queueName){
        def queue = sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder().queueName(queueName).build()
        )

        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(queue.queueUrl())
            .maxNumberOfMessages(5)
            .build()

        List<Message> messages= sqsClient.receiveMessage(receiveMessageRequest).messages()

        messages.each {Message msg ->
            println("Msg received: ${msg}" )
            deleteMessage(queue,msg)
        }
    }

    private void deleteMessage(GetQueueUrlResponse queue, Message message){
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
            .queueUrl(queue.queueUrl())
            .receiptHandle(message.receiptHandle())
            .build()

        sqsClient.deleteMessage(deleteMessageRequest)
    }

    private void send_a_message(String queueName){
        def queue = sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder().queueName(queueName).build()
        )

        def request = SendMessageRequest.builder()
            .queueUrl(queue.queueUrl())
            .messageBody("Hello world!")
            .delaySeconds(10)
            .build()

        def response = sqsClient.sendMessage(request)
        println "Send msg response: ${response}"
    }

    private void createQueue(String queueName){
        def createQueueRequest = CreateQueueRequest.builder().queueName(queueName).build();
        def response = sqsClient.createQueue(createQueueRequest)
        println "Create queue response: ${response}"
    }

    private void listQueues(){
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().build()

        ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest)

        for (String url : listQueuesResponse.queueUrls()) {
            println("List queues: ${url}")
        }
    }
}
