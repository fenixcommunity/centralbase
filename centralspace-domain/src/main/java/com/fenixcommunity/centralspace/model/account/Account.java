package com.fenixcommunity.centralspace.model.account;


import com.fenixcommunity.centralspace.utils.converter.UppercaseConverter;
import com.fenixcommunity.centralspace.model.AbstractBaseEntity;
import com.fenixcommunity.centralspace.model.password.Password;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account")
//@NoArgsConstructor @AllArgsConstructor(access= AccessLevel.PUBLIC)
@Data @Builder @EqualsAndHashCode(callSuper = true) @ToString()
public class Account extends AbstractBaseEntity {

//    AuditingEntityListener co to?
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Convert(converter = UppercaseConverter.class)
    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "email", nullable = false)
    private String email;

    @ToString.Exclude
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Password> passwords;

//     private String email; + tutaj format email (JPA)



}