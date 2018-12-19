package com.cloudbees.jenkins.plugins.bitbucket.endpoints;

import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Map;

/**
 * This class contains a mapping from a Jenkins {@link Result} to a Bitbucket state.
 *
 */
public class BitbucketResultMapping extends AbstractDescribableImpl<BitbucketResultMapping> {
    /** Bitbucket state to set **/
    private final BitbucketState state;
    /** Default description if there is no build description **/
    private final String defaultDescription;


    @DataBoundConstructor
    public BitbucketResultMapping(BitbucketState state, String defaultDescription) {
        this.state = state;
        this.defaultDescription = defaultDescription;
    }

    public BitbucketState getState() {
        return state;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    enum BitbucketState {
        SUCCESSFUL,
        FAILED,
        INPROGRESS,
        // Only valid for Bitbucket Cloud
        STOPPED
    }

    static Map<Result, BitbucketResultMapping> defaultMappings(AbstractBitbucketEndpoint endpoint) {
        Map<Result, BitbucketResultMapping> defaultMappings = Maps.newHashMap();
        defaultMappings.put(Result.SUCCESS, new BitbucketResultMapping(BitbucketState.SUCCESSFUL, "This commit looks good."));
        defaultMappings.put(Result.UNSTABLE, new BitbucketResultMapping(BitbucketState.FAILED, "This commit has test failures."));
        defaultMappings.put(Result.FAILURE, new BitbucketResultMapping(BitbucketState.FAILED, "There was a failure building this commit."));
        defaultMappings.put(Result.ABORTED, new BitbucketResultMapping(BitbucketState.FAILED, "Something is wrong with the build of this commit."));
        defaultMappings.put(null, new BitbucketResultMapping(BitbucketState.INPROGRESS, "The build is in progress..."));
        // Bitbucket Cloud and Server support different build states.
        if (endpoint instanceof BitbucketCloudEndpoint) {
            defaultMappings.put(Result.NOT_BUILT, new BitbucketResultMapping(BitbucketState.STOPPED, "This commit was not built (probably the build was skipped)"));
        } else {
            defaultMappings.put(Result.NOT_BUILT, new BitbucketResultMapping(BitbucketState.SUCCESSFUL, "This commit was not built (probably the build was skipped)"));
        }
        return defaultMappings;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<BitbucketResultMapping> {
        public String getDisplayName() { return null; }

        public ListBoxModel doFillBitbucketStateItems() {
            ListBoxModel listBoxModel = new ListBoxModel();
            for (BitbucketState value : BitbucketState.values()) {
                listBoxModel.add(value.toString());
            }
            return listBoxModel;
        }

    }
}
