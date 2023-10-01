package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    @Order(value = 1)
    @DisplayName("should create user")
    void saveUser() {
        UserDto userDto = makeUserDto("testName", "testEmail@mail.ru");
        userDto = service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u " +
                "from User as u " +
                "where u.email = :email ", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Order(value = 2)
    @DisplayName("should return all users")
    void shouldReturnAllUsers() {
        UserDto userDto = makeUserDto("testName", "testEmail@mail.ru");
        service.createUser(userDto);
        List<UserDto> userDtoList = service.getAllUsers();

        assertThat(userDtoList.size(), equalTo(1));
    }

    @Test
    @Order(value = 3)
    @DisplayName("should update user")
    void shouldUpdateUser() {

        UserDto userDto = makeUserDto("testName", "testEmail@mail.ru");
        userDto = service.createUser(userDto);

        UserDto newUser = new UserDto();
        newUser.setName("newTestName");
        newUser.setEmail("testEmail@mail.ru");
        service.updateUser(newUser, userDto.getId());

        TypedQuery<User> query = em.createQuery("Select u " +
                "from User as u " +
                "where u.email = :email ", User.class);
        User user = query
                .setParameter("email", "testEmail@mail.ru")
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));

    }

    @Test
    @Order(value = 4)
    @DisplayName("should get user by id")
    void shouldGetUserById() {
        UserDto userDto = makeUserDto("testName", "testEmail@mail.ru");
        userDto = service.createUser(userDto);

        UserDto user = service.getUserById(userDto.getId());
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Order(value = 5)
    @DisplayName("should delete user by id")
    void shouldDeleteUserById() {
        UserDto userDto = makeUserDto("testName1", "testEmail1@mail.ru");
        userDto = service.createUser(userDto);
        List<UserDto> userDtoList = service.getAllUsers();
        assertThat(userDtoList.size(), equalTo(1));

        service.deleteUserById(userDto.getId());
        List<UserDto> userDtoListFinal = service.getAllUsers();
        assertThat(userDtoListFinal.size(), equalTo(0));

    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

}
