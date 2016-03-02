package net.cake.idm.core.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * for JPA transaction handling.
 * UserTokenTo class will get mapped to USER_ENTITY_TOKEN table in KEYCLOAK_PROFILE database
 *
 * @author Supun Muthutantri
 * @date 13/02/2016
 */
@Entity
@Table(name = "USER_ENTITY_TOKEN")
public class UserTokenTo {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "CREATED_TIME_TS")
    private Long createdTime;

    @Column(name = "UPDATED_TIME_TS")
    private Long updatedTime;

    @Column(name = "EXPIRATION_TIME")
    private Integer expirationTime;

    public UserTokenTo() {
    }

    public UserTokenTo(String userId, String username, String token, Long createdTime, Integer expirationTime) {
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.createdTime = createdTime;
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "UserTokenTo{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", createdTime=" + createdTime +
                ", UpdatedTime=" + updatedTime +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
