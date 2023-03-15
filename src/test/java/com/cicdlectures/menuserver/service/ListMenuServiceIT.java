package com.cicdlectures.menuserver.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import com.cicdlectures.menuserver.dto.DishDto;
import com.cicdlectures.menuserver.dto.MenuDto;
import com.cicdlectures.menuserver.model.Dish;
import com.cicdlectures.menuserver.model.Menu;
import com.cicdlectures.menuserver.repository.MenuRepository;

// src/test/java/com/cicdlectures/menuserver/controller/MenuControllerIT.java
// Lance l'application sur un port aléatoire.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Indique de relancer l'application à chaque test.
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ListMenuServiceIT {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate template;

  @Autowired
  private MenuRepository menuRepository;

  private final List<Menu> existingMenus = Arrays.asList(
      new Menu(null, "Christmas menu", new HashSet<>(Arrays.asList(new Dish(null, "Turkey", null), new Dish(null, "Pecan Pie", null)))),
      new Menu(null, "New year's eve menu", new HashSet<>(Arrays.asList(new Dish(null, "Potatos", null), new Dish(null, "Tiramisu", null)))));


  @BeforeEach
  public void initDataset() {
    for (Menu menu : existingMenus) {
      menuRepository.save(menu);
    }
  }

  private URL getMenusURL() throws Exception {
    return new URL("http://localhost:" + port + "/menus");
  }

  @Test
  @DisplayName("lists all known menus")
  public void listsAllMenus() throws Exception {
    // On declare la valeur attendue.
    MenuDto[] wantMenus = { 
        new MenuDto(Long.valueOf(1), "Christmas menu",
        new HashSet<DishDto>(Arrays.asList(new DishDto(Long.valueOf(1), "Turkey"), new DishDto(Long.valueOf(2), "Pecan Pie")))),
        new MenuDto(Long.valueOf(2), "New year's eve menu", new HashSet<DishDto>(
            Arrays.asList(new DishDto(Long.valueOf(3), "Potatos"), new DishDto(Long.valueOf(4), "Tiramisu")))) 
        };
        
    ResponseEntity<MenuDto[]> response = this.template.getForEntity(getMenusURL().toString(), MenuDto[].class);
    
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // On list le corps de la reponse.
    MenuDto[] gotMenus = response.getBody();

    assertArrayEquals(wantMenus, gotMenus);
  }
}
