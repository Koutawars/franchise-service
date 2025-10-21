package co.com.bancolombia.metrics.aws;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.metrics.MetricCollection;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MicrometerMetricPublisherTest {

    @Mock
    private MetricCollection metricCollection;
    
    private MeterRegistry meterRegistry;
    private MicrometerMetricPublisher publisher;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        publisher = new MicrometerMetricPublisher(meterRegistry);
    }

    @Test
    void constructor_ShouldCreateInstance() {
        MicrometerMetricPublisher newPublisher = new MicrometerMetricPublisher(meterRegistry);
        
        assertThat(newPublisher).isNotNull();
    }

    @Test
    void publish_ShouldNotThrowException() {
        publisher.publish(metricCollection);
        
        assertThat(publisher).isNotNull();
    }

    @Test
    void close_ShouldNotThrowException() {
        publisher.close();
        
        assertThat(publisher).isNotNull();
    }
}