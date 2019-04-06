/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.inheritance;

import me.lucko.luckperms.common.config.ConfigKeys;
import me.lucko.luckperms.common.config.LuckPermsConfiguration;
import me.lucko.luckperms.common.graph.Graph;
import me.lucko.luckperms.common.graph.TraversalAlgorithm;
import me.lucko.luckperms.common.model.PermissionHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Graph} which represents an "inheritance tree".
 */
public interface InheritanceGraph extends Graph<PermissionHolder> {

    /**
     * Returns an iterable which will traverse this inheritance graph using the specified
     * algorithm starting at the given permission holder start node.
     *
     * @param algorithm the algorithm to use when traversing
     * @param postTraversalSort if a final sort according to inheritance (weight, primary group) rules
     *                          should be performed after the traversal algorithm has completed
     * @param startNode the start node in the inheritance graph
     * @return an iterable
     */
    default Iterable<PermissionHolder> traverse(TraversalAlgorithm algorithm, boolean postTraversalSort, PermissionHolder startNode) {
        Iterable<PermissionHolder> traversal = traverse(algorithm, startNode);

        // perform post traversal sort if needed
        if (postTraversalSort) {
            List<PermissionHolder> resolvedTraversal = new ArrayList<>();
            for (PermissionHolder node : traversal) {
                resolvedTraversal.add(node);
            }

            resolvedTraversal.sort(startNode.getInheritanceComparator());
            traversal = resolvedTraversal;
        }

        return traversal;
    }

    /**
     * Perform a traversal according to the rules defined in the configuration.
     *
     * @param configuration the configuration object
     * @param startNode the start node in the inheritance graph
     * @return an iterable
     */
    default Iterable<PermissionHolder> traverse(LuckPermsConfiguration configuration, PermissionHolder startNode) {
        return traverse(
                configuration.get(ConfigKeys.INHERITANCE_TRAVERSAL_ALGORITHM),
                configuration.get(ConfigKeys.POST_TRAVERSAL_INHERITANCE_SORT),
                startNode
        );
    }

}
