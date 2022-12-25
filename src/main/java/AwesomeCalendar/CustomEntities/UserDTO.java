package AwesomeCalendar.CustomEntities;

import AwesomeCalendar.Entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDTO {
    private Long id;
    private String email;

    private UserDTO() {
    }

    public static UserDTO convertUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    public static List<UserDTO> convertUserListToUserDTOList(List<User> users) {
        List<UserDTO> listUsers = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = UserDTO.convertUserToUserDTO(user);
            listUsers.add(userDTO);
        }
        return listUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (!Objects.equals(id, userDTO.id)) return false;
        return Objects.equals(email, userDTO.email);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
