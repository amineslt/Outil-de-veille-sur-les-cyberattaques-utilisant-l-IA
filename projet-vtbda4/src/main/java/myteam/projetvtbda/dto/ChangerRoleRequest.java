package myteam.projetvtbda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import myteam.projetvtbda.Model.Utilisateur;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangerRoleRequest {
    private Utilisateur.Role role;
}