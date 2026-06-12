package com.mindcurv.b2x.productcatalog.dto.resolver;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.mindcurv.b2x.connector.adapter.DTOReadAdapter;
import com.mindcurv.b2x.connector.product.models.ProductDTO;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("UnitTest")
final class ProductDTOResolverTest {

  @Mock
  private DTOReadAdapter<ProductDTO> readDTOAdapter;

  @Mock
  private ProductDTO productDTO;

  @Spy
  @InjectMocks
  private ProductDTOResolver resolver;

  @BeforeEach
  void setUp() {
    openMocks(this);
    when(readDTOAdapter.findById(anyString())).thenReturn(Optional.of(productDTO));
    when(readDTOAdapter.findByKey(anyString())).thenReturn(Optional.of(productDTO));

  }

  @Nested
  class resolve {

    @Test
    void key() {
      assertTrue(resolver.resolve("key").isPresent());
    }

    @Test
    void failedKey() {
      when(readDTOAdapter.findByKey(anyString())).thenReturn(Optional.empty());
      assertTrue(resolver.resolve("key").isEmpty());
    }

    @Test
    void uuid() {
      assertTrue(resolver.resolve("38dc4668-ab9b-4623-a145-836d024a68f3").isPresent());
    }

    @Test
    void failedUuid() {
      when(readDTOAdapter.findById(anyString())).thenReturn(Optional.empty());
      assertTrue(resolver.resolve("38dc4668-ab9b-4623-a145-836d024a68f3").isEmpty());
    }

    @Test
    void noArgs() {
      assertTrue(resolver.resolve().isEmpty());
    }

  }

}