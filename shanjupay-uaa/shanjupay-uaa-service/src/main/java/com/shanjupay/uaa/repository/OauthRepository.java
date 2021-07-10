package com.shanjupay.uaa.repository;


import com.shanjupay.uaa.domain.OauthClientDetails;

import java.util.List;

public interface OauthRepository {

    OauthClientDetails findOauthClientDetails(String clientId);

    List<OauthClientDetails> findAllOauthClientDetails();

    void updateOauthClientDetailsArchive(String clientId, boolean archive);

    void saveOauthClientDetails(OauthClientDetails clientDetails);

    
}