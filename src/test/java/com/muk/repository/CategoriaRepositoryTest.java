package com.muk.repository;

import com.muk.entities.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria bebidas;
    private Categoria platos;
    private Categoria postres;

    @BeforeEach
    void setUp() {
        bebidas = new Categoria(null, "Bebidas", "Bebidas frias y calientes");
        platos = new Categoria(null, "Platos", "Platos fuertes");
        postres = new Categoria(null, "Postres", "Dulces");
        categoriaRepository.saveAll(List.of(bebidas, platos, postres));
        categoriaRepository.flush();
    }

    @Test
    void guardarPersisteYGeneraId() {
        // Arrange
        Categoria nueva = new Categoria(null, "Entradas", "Para compartir");
        // Act
        Categoria guardada = categoriaRepository.save(nueva);
        // Assert
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Entradas");
        assertThat(guardada.getDescription()).isEqualTo("Para compartir");
    }

    @Test
    void findByIdDevuelveCategoriaCuandoExiste() {
        // Arrange
        Long id = bebidas.getId();
        // Act
        Optional<Categoria> resultado = categoriaRepository.findById(id);
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Bebidas");
    }

    @Test
    void findByIdVacíoCuandoNoExiste() {
        // Act
        Optional<Categoria> resultado = categoriaRepository.findById(9_999L);
        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void actualizarModificaDatosPersistidos() {
        // Arrange
        bebidas.setDescription("Actualizada");
        // Act
        categoriaRepository.saveAndFlush(bebidas);
        // Assert
        Optional<Categoria> recargada = categoriaRepository.findById(bebidas.getId());
        assertThat(recargada).isPresent();
        assertThat(recargada.get().getDescription()).isEqualTo("Actualizada");
    }

    @Test
    void deleteByIdEliminaYCambiaFindById() {
        // Arrange
        Long id = postres.getId();
        // Act
        categoriaRepository.deleteById(id);
        categoriaRepository.flush();
        // Assert
        assertThat(categoriaRepository.findById(id)).isEmpty();
    }

    @Test
    void findAllIncluyeDatosDelSetUp() {
        // Act
        List<Categoria> todas = categoriaRepository.findAll();
        // Assert
        assertThat(todas).hasSize(3);
    }

    @Test
    void findByNombreIgnoreCaseEncuentraCoincidencia() {
        // Act
        Optional<Categoria> resultado = categoriaRepository.findByNombreIgnoreCase("bebidas");
        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Bebidas");
    }

    @Test
    void findByNombreIgnoreCaseVacíoSiNoHayCoincidencia() {
        // Act
        Optional<Categoria> resultado = categoriaRepository.findByNombreIgnoreCase("CategoriaInexistente");
        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void findAllByOrderByNombreAscOrdenaAlfabeticamente() {
        // Act
        List<Categoria> ordenadas = categoriaRepository.findAllByOrderByNombreAsc();
        // Assert
        assertThat(ordenadas).hasSize(3);
        assertThat(ordenadas)
                .extracting(Categoria::getNombre)
                .containsExactly("Bebidas", "Platos", "Postres");
    }

    @Test
    void guardarFallaCuandoNombreEsNull() {
        // Arrange
        Categoria invalida = new Categoria(null, null, "Sin nombre");
        // Act + Assert
        assertThatThrownBy(() -> {
            categoriaRepository.saveAndFlush(invalida);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void guardarFallaCuandoNombreEstaDuplicado() {
        // Arrange
        Categoria duplicada = new Categoria(null, "Bebidas", "Nombre repetido");
        // Act + Assert
        assertThatThrownBy(() -> {
            categoriaRepository.saveAndFlush(duplicada);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void guardarFallaCuandoNombreSuperaLongitudMaxima() {
        // Arrange
        String nombreLargo = "A".repeat(51);
        Categoria invalida = new Categoria(null, nombreLargo, "Nombre demasiado largo");
        // Act + Assert
        assertThatThrownBy(() -> {
            categoriaRepository.saveAndFlush(invalida);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void guardarFallaCuandoDescriptionSuperaLongitudMaxima() {
        // Arrange
        String descripcionLarga = "B".repeat(256);
        Categoria invalida = new Categoria(null, "Entradas", descripcionLarga);
        // Act + Assert
        assertThatThrownBy(() -> {
            categoriaRepository.saveAndFlush(invalida);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
