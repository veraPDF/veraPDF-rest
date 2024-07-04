/**
 *
 */
package org.verapdf.rest.resources;

import java.util.*;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.ProfileDetails;
import org.verapdf.pdfa.validation.profiles.ProfileDirectory;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.Rule;
import org.verapdf.pdfa.validation.profiles.RuleId;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
@Singleton
public class ProfileResource {

    public static final String PROFILE_ID_PARAMETER_DESCRIPTION = "The String ID of the Validation profile " +
            "(arlington1.0, arlington1.1, arlington1.2, arlington1.3, arlington1.4, arlington1.5, arlington1.6, arlington1.7, arlington2.0)";
    
    public static final String PROFILE_NOT_FOUND_MESSAGE = "The requested profile was not found";
    
    private static final ProfileDirectory DIRECTORY = Profiles.getVeraProfileDirectory();
    private static final Set<ProfileDetails> DETAILS = new HashSet<>();

    static {
        for (ValidationProfile profile : DIRECTORY.getValidationProfiles()) {
            DETAILS.add(profile.getDetails());
        }
    }

    /**
     * @return the set of validation profile details
     */
    @GET
    @Operation(summary = "Returns the set of validation profile details for profiles supported by the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A profile list was returned successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDetails.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ProfileDetails.class)) }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Set<ProfileDetails> getProfileDetails() {
        return DETAILS;
    }

    /**
     * @return the Set of Validation Profile IDs
     */
    @GET
    @Path("/ids")
    @Operation(summary = "Gets the set of IDs for the supported validation profiles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The set of validation profile IDs was returned successfully.", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))),
                    @Content(mediaType = "application/xml", array = @ArraySchema(schema = @Schema(implementation = String.class))) }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Set<String> getProfileIds() {
        return DIRECTORY.getValidationProfileIds();
    }

    /**
     * @return the Set of Flavours
     */
    @GET
    @Path("/flavours")
    @Operation(summary = "Returns the set of supported flavours supported by the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The set of supported flavours was returned successfully.", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PDFAFlavour.class))),
                    @Content(mediaType = "application/xml", array = @ArraySchema(schema = @Schema(implementation = PDFAFlavour.class))) }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Set<PDFAFlavour> getFlavours() {
        return DIRECTORY.getPDFAFlavours();
    }

    /**
     * @param profileId
     *                  the String id of the Validation profile
     * @return a validation profile selected by id
     */
    @GET
    @Path("/{profileId}")
    @Operation(summary = "Returns the validation profile selected by id (validation flavour)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested profile was found and returned.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationProfile.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ValidationProfile.class)) }),
            @ApiResponse(responseCode = "404", description = PROFILE_NOT_FOUND_MESSAGE, content = {
                    @Content(mediaType = "application/json"),
                    @Content(mediaType = "application/xml")
            }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static ValidationProfile getProfile(
            @Parameter(description = PROFILE_ID_PARAMETER_DESCRIPTION) @PathParam("profileId") String profileId) {
        try {
            return DIRECTORY.getValidationProfileById(profileId);
        } catch (NoSuchElementException e) {
            throw new NotFoundException(PROFILE_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * @param profileId
     *                  the String id of the Validation profile
     * @return the {@link java.util.Set} of
     *         {@link org.verapdf.pdfa.validation.profiles.RuleId}s for the selected
     *         Validation Profile
     */
    @GET
    @Path("/{profileId}/ruleids")
    @Operation(summary = "Returns the full set of rule IDs for the selected validation profiles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RuleId.class))),
                    @Content(mediaType = "application/xml", array = @ArraySchema(schema = @Schema(implementation = RuleId.class))) }),
            @ApiResponse(responseCode = "404", description = PROFILE_NOT_FOUND_MESSAGE, content = {
                    @Content(mediaType = "application/json"),
                    @Content(mediaType = "application/xml")
            }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Set<RuleId> getProfileRules(
            @Parameter(description = PROFILE_ID_PARAMETER_DESCRIPTION) @PathParam("profileId") String profileId) {
        SortedSet<RuleId> ids = new TreeSet<>(new Profiles.RuleIdComparator());
        try {
            for (Rule rule : DIRECTORY.getValidationProfileById(profileId).getRules()) {
                ids.add(rule.getRuleId());
            }
        } catch (NoSuchElementException e) {
            throw new NotFoundException(PROFILE_NOT_FOUND_MESSAGE);
        }
        return ids;
    }

    /**
     * @param profileId
     *                  the String id of the Validation profile
     * @param clause
     *                  a {@link java.lang.String} identifying the profile clause to
     *                  return the Rules for
     * @return the {@link java.util.Set} of
     *         {@link org.verapdf.pdfa.validation.profiles.Rule}s for the selected
     *         profile and clause
     */
    @GET
    @Path("/{profileId}/{clause}")
    @Operation(summary = "Gets the set of rules for the selected profile and clause")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Rule.class))),
                    @Content(mediaType = "application/xml", array = @ArraySchema(schema = @Schema(implementation = Rule.class))) }),
            @ApiResponse(responseCode = "404", description = PROFILE_NOT_FOUND_MESSAGE, content = {
                    @Content(mediaType = "application/json"),
                    @Content(mediaType = "application/xml")
            }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Set<Rule> getRulesForClause(@Parameter(description = PROFILE_ID_PARAMETER_DESCRIPTION) @PathParam("profileId") String profileId,
            @Parameter(description = "A string identifying the profile clause to return the rules for.") @PathParam("clause") String clause) {
        SortedSet<Rule> rules = new TreeSet<>(new Profiles.RuleComparator());
        try {
            for (Rule rule : DIRECTORY.getValidationProfileById(profileId).getRules()) {
                if (rule.getRuleId().getClause().equalsIgnoreCase(clause)) {
                    rules.add(rule);
                }
            }
        } catch (NoSuchElementException e) {
            throw new NotFoundException(PROFILE_NOT_FOUND_MESSAGE);
        }
        return rules;
    }
}
