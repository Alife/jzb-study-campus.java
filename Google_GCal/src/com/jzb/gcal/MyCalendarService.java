/**
 * 
 */
package com.jzb.gcal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.PrivateKey;
import java.util.Set;

import com.google.gdata.client.AuthTokenFactory;
import com.google.gdata.client.CookieManager;
import com.google.gdata.client.Query;
import com.google.gdata.client.AuthTokenFactory.AuthToken;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.batch.BatchInterruptedException;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.http.GoogleGDataRequest.GoogleCookie;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.IEntry;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.Link;
import com.google.gdata.data.introspection.IServiceDocument;
import com.google.gdata.data.introspection.ServiceDocument;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

/**
 * @author n000013
 * 
 */
public class MyCalendarService extends CalendarService {

    private CalendarService m_service;

    /**
     * @param applicationName
     */
    public MyCalendarService(CalendarService wrappedService) {
        super("");
        m_service = wrappedService;
    }

    /**
     * @param cookie
     * @see com.google.gdata.client.GoogleService#addCookie(com.google.gdata.client.http.GoogleGDataRequest.GoogleCookie)
     */
    public void addCookie(GoogleCookie cookie) {
        m_service.addCookie(cookie);
    }

    /**
     * @param <F>
     * @param feedUrl
     * @param inputFeed
     * @return
     * @throws IOException
     * @throws ServiceException
     * @throws BatchInterruptedException
     * @see com.google.gdata.client.Service#batch(java.net.URL, com.google.gdata.data.BaseFeed)
     */
    public <F extends BaseFeed<?, ?>> F batch(URL feedUrl, F inputFeed) throws IOException, ServiceException, BatchInterruptedException {
        return m_service.batch(feedUrl, inputFeed);
    }

    /**
     * @param feedUrl
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createBatchRequest(java.net.URL)
     */
    public GDataRequest createBatchRequest(URL feedUrl) throws IOException, ServiceException {
        return m_service.createBatchRequest(feedUrl);
    }

    /**
     * @param entryUrl
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createDeleteRequest(java.net.URL)
     */
    public GDataRequest createDeleteRequest(URL entryUrl) throws IOException, ServiceException {
        return m_service.createDeleteRequest(entryUrl);
    }

    /**
     * @param entryUrl
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createEntryRequest(java.net.URL)
     */
    public GDataRequest createEntryRequest(URL entryUrl) throws IOException, ServiceException {
        return m_service.createEntryRequest(entryUrl);
    }

    /**
     * @param query
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createFeedRequest(com.google.gdata.client.Query)
     */
    public GDataRequest createFeedRequest(Query query) throws IOException, ServiceException {
        return m_service.createFeedRequest(query);
    }

    /**
     * @param feedUrl
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createFeedRequest(java.net.URL)
     */
    public GDataRequest createFeedRequest(URL feedUrl) throws IOException, ServiceException {
        return m_service.createFeedRequest(feedUrl);
    }

    /**
     * @param feedUrl
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createInsertRequest(java.net.URL)
     */
    public GDataRequest createInsertRequest(URL feedUrl) throws IOException, ServiceException {
        return m_service.createInsertRequest(feedUrl);
    }

    /**
     * @param type
     * @param requestUrl
     * @param contentType
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#createRequest(com.google.gdata.client.Service.GDataRequest.RequestType, java.net.URL, com.google.gdata.util.ContentType)
     */
    public GDataRequest createRequest(RequestType type, URL requestUrl, ContentType contentType) throws IOException, ServiceException {
        return m_service.createRequest(type, requestUrl, contentType);
    }

    /**
     * @param entryUrl
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#createUpdateRequest(java.net.URL)
     */
    public GDataRequest createUpdateRequest(URL entryUrl) throws IOException, ServiceException {
        return m_service.createUpdateRequest(entryUrl);
    }

    /**
     * @param resourceUri
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#delete(java.net.URI)
     */
    public void delete(URI resourceUri) throws IOException, ServiceException {
        m_service.delete(resourceUri);
    }

    /**
     * @param resourceUri
     * @param etag
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#delete(java.net.URI, java.lang.String)
     */
    public void delete(URI resourceUri, String etag) throws IOException, ServiceException {
        m_service.delete(resourceUri, etag);
    }

    /**
     * @param entryUrl
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#delete(java.net.URL)
     */
    public void delete(URL entryUrl) throws IOException, ServiceException {
        m_service.delete(entryUrl);
    }

    /**
     * @param entryUrl
     * @param etag
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#delete(java.net.URL, java.lang.String)
     */
    public void delete(URL entryUrl, String etag) throws IOException, ServiceException {
        m_service.delete(entryUrl, etag);
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return m_service.equals(obj);
    }

    /**
     * @param username
     * @param password
     * @param captchaToken
     * @param captchaAnswer
     * @param serviceName
     * @param applicationName
     * @return
     * @throws AuthenticationException
     * @see com.google.gdata.client.GoogleService#getAuthToken(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String getAuthToken(String username, String password, String captchaToken, String captchaAnswer, String serviceName, String applicationName) throws AuthenticationException {
        return m_service.getAuthToken(username, password, captchaToken, captchaAnswer, serviceName, applicationName);
    }

    /**
     * @return
     * @see com.google.gdata.client.GoogleService#getAuthTokenFactory()
     */
    public AuthTokenFactory getAuthTokenFactory() {
        return m_service.getAuthTokenFactory();
    }

    /**
     * @return
     * @see com.google.gdata.client.Service#getContentType()
     */
    public ContentType getContentType() {
        return m_service.getContentType();
    }

    /**
     * @return
     * @see com.google.gdata.client.GoogleService#getCookieManager()
     */
    public CookieManager getCookieManager() {
        return m_service.getCookieManager();
    }

    /**
     * @return
     * @see com.google.gdata.client.GoogleService#getCookies()
     */
    public Set<GoogleCookie> getCookies() {
        return m_service.getCookies();
    }

    /**
     * @see com.jzb.gcal.MyCalendarService#getEntry(java.net.URL, java.lang.Class)
     */
    @Override
    public <E extends IEntry> E getEntry(URL entryUrl, Class<E> entryClass) throws IOException, ServiceException {
        // TODO Auto-generated method stub
        return m_service.getEntry(entryUrl, entryClass);
    }

    /**
     * @param <E>
     * @param entryUrl
     * @param entryClass
     * @param ifModifiedSince
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#getEntry(java.net.URL, java.lang.Class, com.google.gdata.data.DateTime)
     */
    @Override
    public <E extends IEntry> E getEntry(URL entryUrl, Class<E> entryClass, DateTime ifModifiedSince) throws IOException, ServiceException {
        return m_service.getEntry(entryUrl, entryClass, ifModifiedSince);
    }

    /**
     * @param <E>
     * @param entryUrl
     * @param entryClass
     * @param etag
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#getEntry(java.net.URL, java.lang.Class, java.lang.String)
     */
    @Override
    public <E extends IEntry> E getEntry(URL entryUrl, Class<E> entryClass, String etag) throws IOException, ServiceException {
        return m_service.getEntry(entryUrl, entryClass, etag);
    }

    /**
     * @return
     * @see com.google.gdata.client.Service#getExtensionProfile()
     */
    public ExtensionProfile getExtensionProfile() {
        return m_service.getExtensionProfile();
    }

    /**
     * @param <F>
     * @param query
     * @param feedClass
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#getFeed(com.google.gdata.client.Query, java.lang.Class)
     */
    @Override
    public <F extends IFeed> F getFeed(Query query, Class<F> feedClass) throws IOException, ServiceException {
        return m_service.getFeed(query, feedClass);
    }

    /**
     * @param <F>
     * @param query
     * @param feedClass
     * @param ifModifiedSince
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#getFeed(com.google.gdata.client.Query, java.lang.Class, com.google.gdata.data.DateTime)
     */
    @Override
    public <F extends IFeed> F getFeed(Query query, Class<F> feedClass, DateTime ifModifiedSince) throws IOException, ServiceException {
        return m_service.getFeed(query, feedClass, ifModifiedSince);
    }

    /**
     * @param <F>
     * @param query
     * @param feedClass
     * @param etag
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#getFeed(com.google.gdata.client.Query, java.lang.Class, java.lang.String)
     */
    @Override
    public <F extends IFeed> F getFeed(Query query, Class<F> feedClass, String etag) throws IOException, ServiceException {
        return m_service.getFeed(query, feedClass, etag);
    }

    /**
     * @param <F>
     * @param feedUrl
     * @param feedClass
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#getFeed(java.net.URL, java.lang.Class)
     */
    @Override
    public <F extends IFeed> F getFeed(URL feedUrl, Class<F> feedClass) throws IOException, ServiceException {
        return m_service.getFeed(feedUrl, feedClass);
    }

    /**
     * @param <F>
     * @param feedUrl
     * @param feedClass
     * @param ifModifiedSince
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#getFeed(java.net.URL, java.lang.Class, com.google.gdata.data.DateTime)
     */
    @Override
    public <F extends IFeed> F getFeed(URL feedUrl, Class<F> feedClass, DateTime ifModifiedSince) throws IOException, ServiceException {
        return m_service.getFeed(feedUrl, feedClass, ifModifiedSince);
    }

    /**
     * @param <F>
     * @param feedUrl
     * @param feedClass
     * @param etag
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#getFeed(java.net.URL, java.lang.Class, java.lang.String)
     */
    @Override
    public <F extends IFeed> F getFeed(URL feedUrl, Class<F> feedClass, String etag) throws IOException, ServiceException {
        return m_service.getFeed(feedUrl, feedClass, etag);
    }

    /**
     * @return
     * @see com.google.gdata.client.Service#getRequestFactory()
     */
    public GDataRequestFactory getRequestFactory() {
        return m_service.getRequestFactory();
    }

    /**
     * @return
     * @see com.google.gdata.client.calendar.CalendarService#getServiceVersion()
     */
    public String getServiceVersion() {
        return m_service.getServiceVersion();
    }

    /**
     * @param link
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#getStreamFromLink(com.google.gdata.data.Link)
     */
    public InputStream getStreamFromLink(Link link) throws IOException, ServiceException {
        return m_service.getStreamFromLink(link);
    }

    /**
     * @return
     * @see com.google.gdata.client.GoogleService#handlesCookies()
     */
    public boolean handlesCookies() {
        return m_service.handlesCookies();
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return m_service.hashCode();
    }

    /**
     * @param <E>
     * @param feedUrl
     * @param entry
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#insert(java.net.URL, com.google.gdata.data.BaseEntry)
     */
    public <E extends BaseEntry<?>> E insert(URL feedUrl, E entry) throws IOException, ServiceException {
        return m_service.insert(feedUrl, entry);
    }

    /**
     * @param <S>
     * @param feedUrl
     * @param serviceClass
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#introspect(java.net.URL, java.lang.Class)
     */
    @Override
    public <S extends IServiceDocument> S introspect(URL feedUrl, Class<S> serviceClass) throws IOException, ServiceException {
        return m_service.introspect(feedUrl, serviceClass);
    }

    /**
     * @param <F>
     * @param query
     * @param feedClass
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#query(com.google.gdata.client.Query, java.lang.Class)
     */
    @Override
    public <F extends IFeed> F query(Query query, Class<F> feedClass) throws IOException, ServiceException {
        return m_service.query(query, feedClass);
    }

    /**
     * @param <F>
     * @param query
     * @param feedClass
     * @param ifModifiedSince
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#query(com.google.gdata.client.Query, java.lang.Class, com.google.gdata.data.DateTime)
     */
    @Override
    public <F extends IFeed> F query(Query query, Class<F> feedClass, DateTime ifModifiedSince) throws IOException, ServiceException {
        return m_service.query(query, feedClass, ifModifiedSince);
    }

    /**
     * @param <F>
     * @param query
     * @param feedClass
     * @param etag
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#query(com.google.gdata.client.Query, java.lang.Class, java.lang.String)
     */
    @Override
    public <F extends IFeed> F query(Query query, Class<F> feedClass, String etag) throws IOException, ServiceException {
        return m_service.query(query, feedClass, etag);
    }

    /**
     * @param acceptedLanguages
     * @see com.google.gdata.client.Service#setAcceptLanguage(java.lang.String)
     */
    public void setAcceptLanguage(String acceptedLanguages) {
        m_service.setAcceptLanguage(acceptedLanguages);
    }

    /**
     * @param token
     * @see com.google.gdata.client.GoogleService#setAuthSubToken(java.lang.String)
     */
    public void setAuthSubToken(String token) {
        m_service.setAuthSubToken(token);
    }

    /**
     * @param token
     * @param key
     * @see com.google.gdata.client.GoogleService#setAuthSubToken(java.lang.String, java.security.PrivateKey)
     */
    public void setAuthSubToken(String token, PrivateKey key) {
        m_service.setAuthSubToken(token, key);
    }

    /**
     * @param authTokenFactory
     * @see com.google.gdata.client.GoogleService#setAuthTokenFactory(com.google.gdata.client.AuthTokenFactory)
     */
    public void setAuthTokenFactory(AuthTokenFactory authTokenFactory) {
        m_service.setAuthTokenFactory(authTokenFactory);
    }

    /**
     * @param timeout
     * @see com.google.gdata.client.Service#setConnectTimeout(int)
     */
    public void setConnectTimeout(int timeout) {
        m_service.setConnectTimeout(timeout);
    }

    /**
     * @param contentType
     * @see com.google.gdata.client.Service#setContentType(com.google.gdata.util.ContentType)
     */
    public void setContentType(ContentType contentType) {
        m_service.setContentType(contentType);
    }

    /**
     * @param cookieManager
     * @see com.google.gdata.client.GoogleService#setCookieManager(com.google.gdata.client.CookieManager)
     */
    public void setCookieManager(CookieManager cookieManager) {
        m_service.setCookieManager(cookieManager);
    }

    /**
     * @param v
     * @see com.google.gdata.client.Service#setExtensionProfile(com.google.gdata.data.ExtensionProfile)
     */
    public void setExtensionProfile(ExtensionProfile v) {
        m_service.setExtensionProfile(v);
    }

    /**
     * @param handlesCookies
     * @see com.google.gdata.client.GoogleService#setHandlesCookies(boolean)
     */
    public void setHandlesCookies(boolean handlesCookies) {
        m_service.setHandlesCookies(handlesCookies);
    }

    /**
     * @param parameters
     * @param signer
     * @throws OAuthException
     * @see com.google.gdata.client.GoogleService#setOAuthCredentials(com.google.gdata.client.authn.oauth.OAuthParameters, com.google.gdata.client.authn.oauth.OAuthSigner)
     */
    public void setOAuthCredentials(OAuthParameters parameters, OAuthSigner signer) throws OAuthException {
        m_service.setOAuthCredentials(parameters, signer);
    }

    /**
     * @param timeout
     * @see com.google.gdata.client.Service#setReadTimeout(int)
     */
    public void setReadTimeout(int timeout) {
        m_service.setReadTimeout(timeout);
    }

    /**
     * @param requestFactory
     * @see com.google.gdata.client.Service#setRequestFactory(com.google.gdata.client.Service.GDataRequestFactory)
     */
    public void setRequestFactory(GDataRequestFactory requestFactory) {
        m_service.setRequestFactory(requestFactory);
    }

    /**
     * @param request
     * @see com.google.gdata.client.Service#setTimeouts(com.google.gdata.client.Service.GDataRequest)
     */
    public void setTimeouts(GDataRequest request) {
        m_service.setTimeouts(request);
    }

    /**
     * @param username
     * @param password
     * @throws AuthenticationException
     * @see com.google.gdata.client.GoogleService#setUserCredentials(java.lang.String, java.lang.String)
     */
    public void setUserCredentials(String username, String password) throws AuthenticationException {
        m_service.setUserCredentials(username, password);
    }

    /**
     * @param username
     * @param password
     * @param captchaToken
     * @param captchaAnswer
     * @throws AuthenticationException
     * @see com.google.gdata.client.GoogleService#setUserCredentials(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setUserCredentials(String username, String password, String captchaToken, String captchaAnswer) throws AuthenticationException {
        m_service.setUserCredentials(username, password, captchaToken, captchaAnswer);
    }

    /**
     * @param token
     * @see com.google.gdata.client.GoogleService#setUserToken(java.lang.String)
     */
    public void setUserToken(String token) {
        m_service.setUserToken(token);
    }

    /**
     * @param newToken
     * @see com.google.gdata.client.GoogleService#tokenChanged(com.google.gdata.client.AuthTokenFactory.AuthToken)
     */
    public void tokenChanged(AuthToken newToken) {
        m_service.tokenChanged(newToken);
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return m_service.toString();
    }

    /**
     * @param <E>
     * @param entryUrl
     * @param entry
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.GoogleService#update(java.net.URL, com.google.gdata.data.BaseEntry)
     */
    public <E extends BaseEntry<?>> E update(URL entryUrl, E entry) throws IOException, ServiceException {
        return m_service.update(entryUrl, entry);
    }

    /**
     * @param <E>
     * @param entryUrl
     * @param entry
     * @param etag
     * @return
     * @throws IOException
     * @throws ServiceException
     * @see com.google.gdata.client.Service#update(java.net.URL, com.google.gdata.data.BaseEntry, java.lang.String)
     */
    public <E extends BaseEntry<?>> E update(URL entryUrl, E entry, String etag) throws IOException, ServiceException {
        return m_service.update(entryUrl, entry, etag);
    }

    /**
     * 
     * @see com.google.gdata.client.Service#useSsl()
     */
    public void useSsl() {
        m_service.useSsl();
    }

}
