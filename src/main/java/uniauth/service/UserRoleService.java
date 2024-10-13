package uniauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uniauth.jpa.repository.AppMemberRepository;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.AppRoleRepository;

@RequiredArgsConstructor
@Service
public class UserRoleService {
    private final AppMemberRepository appMemberRepository;
    private final AppRepository appRepository;
    private final AppRoleRepository appRoleRepository;

    public boolean isPlatformAdmin(Integer userId) {
        var app = appRepository.findByCode("uniauth").orElseThrow();
        var appMemberOptional = appMemberRepository.findByUserIdAndAppId(userId, app.getId());
        if (appMemberOptional.isEmpty()) {
            return false;
        }
        var appMember = appMemberOptional.get();
        return appMember.getRoles().contains(appRoleRepository.findById(1).orElseThrow());
    }


    public boolean isAppAdmin(Integer userId, Integer appId) {
        var appMemberOptional = appMemberRepository.findByUserIdAndAppId(userId, appId);
        if (appMemberOptional.isEmpty()) {
            return false;
        }
        var appMember = appMemberOptional.get();
        return appMember.getRoles().contains(appRoleRepository.findById(1).orElseThrow());
    }

}
