package com.mbo.perfumery.service;

import com.mbo.perfumery.dto.PerfumDto;
import com.mbo.perfumery.enums.Category;
import com.mbo.perfumery.mapper.DateMapper;
import com.mbo.perfumery.mapper.PerfumMapper;
import com.mbo.perfumery.mapper.PerfumMapperImpl;
import com.mbo.perfumery.model.Perfum;
import com.mbo.perfumery.repository.PerfumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfumServiceImplTest {

    @Mock
    PerfumRepository perfumRepository;

    PerfumMapper perfumMapper;

    PerfumService perfumService;

    List<Perfum> perfums;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        perfumMapper =  new PerfumMapperImpl(new DateMapper());
        perfumService = new PerfumServiceImpl(perfumRepository, perfumMapper);

        perfums = this.initSomePerfum();
    }

    @Test
    void getAllParfum() {

        //Given
        List<PerfumDto> perfumDtos = new ArrayList<>();

        //When
        when(perfumRepository.findAll()).thenReturn(perfums);

        //Then
        perfumDtos = perfumService.getAllParfum();
        assertEquals(perfums.size(), perfumDtos.size());
    }

    @Test
    void getParfumByCategory() {

        //Given
        List<PerfumDto> perfumDtos = new ArrayList<>();

        //When
        when(perfumRepository.findByCategory(any())).thenReturn(perfums);

        //Then
        perfumDtos = perfumService.getParfumByCategory(Category.Men);
        assertEquals(perfums.size(), perfumDtos.size());
    }

    List<Perfum> initSomePerfum()
    {

        return  Stream.of(new Perfum().builder().name("Parfum 1").description("Parfum de test").category(Category.Men).build(),
                new Perfum().builder().name("Parfum 2").description("Parfum de test").category(Category.Women).build(),
                new Perfum().builder().name("Parfum 3").description("Parfum de test").category(Category.Men).build(),
                new Perfum().builder().name("Parfum 4").description("Parfum de test").category(Category.Women).build(),
                new Perfum().builder().name("Parfum 5").description("Parfum de test").category(Category.Men).build()).collect(Collectors.toList());
    }

    @Test
    void createPerfum() {
        //Given
        PerfumDto perfumToCreate = new PerfumDto().builder()
                .name("Perfum 1")
                .description("Test's perfum")
                .category(Category.Men)
                .price(50D).build();

        //When
        when(perfumRepository.save(any())).thenReturn(perfumMapper.perfumDtoToPerfum(perfumToCreate));

        //Then
        PerfumDto savedPerfumDto = perfumService.createPerfum(perfumToCreate);
        assertEquals(perfumToCreate.getName(), savedPerfumDto.getName());
        assertEquals(perfumToCreate.getDescription(), savedPerfumDto.getDescription());
        assertEquals(perfumToCreate.getCategory(), savedPerfumDto.getCategory());
    }

    @Test
    void updatePerfum() {
        //given
        Optional<Perfum> perfumToUpdate = Optional.of(new Perfum().builder()
                .name("Perfum 2")
                .description("Test's perfum for women")
                .category(Category.Women)
                .price(70D).build());

        PerfumDto perfumDto= new PerfumDto().builder()
                .name("Perfum 2.2")
                .description("Test's perfum for women (Price changed)")
                .category(Category.Women)
                .price(80D).build();

        //When
        when(perfumRepository.findById(any())).thenReturn(perfumToUpdate);
        when(perfumRepository.save(any())).thenReturn(perfumMapper.perfumDtoToPerfum(perfumDto));

        //Then
        PerfumDto updatedPerfumDto = perfumService.updatePerfum(UUID.randomUUID(), perfumDto);
        assertEquals(perfumDto.getName(), updatedPerfumDto.getName());
        assertEquals(perfumDto.getDescription(), updatedPerfumDto.getDescription());
        assertEquals(perfumDto.getPrice(), updatedPerfumDto.getPrice());
    }

    @Test
    void deletePerfum() {
        //Then, When
        perfumService.deletePerfum(UUID.randomUUID());
        verify(perfumRepository, times(1)).deleteById(any());
    }

    @Test
    void checkStockQuantity() {
        //Given
        Optional<Perfum> perfum = Optional.of(Perfum.builder()
                .name("Pefum x")
                .category(Category.Women)
                .stockQuantity(50)
                .build());

        //When
        when(perfumRepository.findById(any())).thenReturn(perfum);

        //Then
        boolean isAvailable = perfumService.checkStockQuantity(UUID.randomUUID(), 2);
        assertEquals(true, isAvailable);
        assertEquals(48, perfum.get().getStockQuantity());
    }

    @Test
    void getPerfumById()
    {
        //Given
        Optional<Perfum> perfum = Optional.of(Perfum.builder()
                .name("Perfum x")
                .description("It's a test perfum")
                .category(Category.Men)
                .price(100D)
                .stockQuantity(20)
                .build());
        //When
        when(perfumRepository.findById(any())).thenReturn(perfum);

        //Then
        PerfumDto perfumDto = perfumService.getPerfumById(UUID.randomUUID());
        assertEquals(perfumDto.getName(), perfum.get().getName());
        assertEquals(perfumDto.getPrice(), perfum.get().getPrice());
    }

    @Test
    void getPerfumByIdNotFound()
    {
        //Given
        Optional<Perfum> emptyperfum = Optional.empty();

        //When
        when(perfumRepository.findById(any())).thenReturn(emptyperfum);

        //Then
        assertThrows(RuntimeException.class,()->{
            perfumService.getPerfumById(UUID.randomUUID());
        });
    }
}