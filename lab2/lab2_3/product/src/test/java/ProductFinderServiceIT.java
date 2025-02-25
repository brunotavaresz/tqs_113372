import com.example.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class ProductFinderServiceIT {

    @Test
    void findProductDetails_validId_returnsProduct() throws IOException {
        ISimpleHttpClient httpClient = new TqsBasicHttpClient();
        ProductFinderService service = new ProductFinderService(httpClient);

        Optional<Product> product = service.findProductDetails(3);

        assertTrue(product.isPresent());
        assertEquals(3, product.get().getId());
        assertEquals("Mens Cotton Jacket", product.get().getTitle());
    }

    @Test
    void findProductDetails_invalidId_returnsEmptyOptional() throws IOException {
        ISimpleHttpClient httpClient = new TqsBasicHttpClient();
        ProductFinderService service = new ProductFinderService(httpClient);

        Optional<Product> product = service.findProductDetails(300);

        assertTrue(product.isEmpty());
    }
}
