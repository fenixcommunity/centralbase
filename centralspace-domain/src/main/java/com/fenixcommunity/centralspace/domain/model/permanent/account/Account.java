package com.fenixcommunity.centralspace.domain.model.permanent.account;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fenixcommunity.centralspace.domain.converter.UppercaseConverter;
import com.fenixcommunity.centralspace.domain.core.AccountEntityListener;
import com.fenixcommunity.centralspace.domain.model.permanent.AbstractBaseEntity;
import com.fenixcommunity.centralspace.domain.model.permanent.password.Password;
import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.PESEL;

// @NamedQuery(name = "Account.findByAccountId" ...
@Entity @Table(name = "account") @EntityListeners(AccountEntityListener.class)
// @DynamicUpdate -> when we update login then update Account set login=?, mail=? ... where id=?
// if we want update only login column then @DynamicUpdate -> update Account set name=? where id=?
// use only when we have a lot of columns -> in other cases performance overhead
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(callSuper = true) @ToString() @FieldDefaults(level = PRIVATE)
public class Account extends AbstractBaseEntity {

    //todo AuditingEntityListener add some things
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JMap("uniqueLogin")
    @Convert(converter = UppercaseConverter.class)
    @Length(min = 2, max = 15)
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    //TODO walidacja i opakowac
    @JMap
    @Column(name = "mail", nullable = false)
    private String mail;

    @PESEL
    @Column(name = "pesel")
    private String pesel;

    @NIP
    @Column(name = "nip")
    private String nip;

    @Singular @ToString.Exclude
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    //todo Set<...  sort by ...
    private List<Password> passwords;

//     TODO [!] TO Avoid lazyInitException:
//    - @Transactional
//    - spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
//    - " JOIN FETCH a.address " +
    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    private String createdBy; //todo add Admin
}
