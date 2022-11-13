/*
 * Copyright (C) 2022 CUJAE.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package cu.edu.cujae.graphy.core.defaults;

import cu.edu.cujae.graphy.core.Edge;
import cu.edu.cujae.graphy.core.Node;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This is the default implementation of the {@link Node} interface.It provides some default operations for nodes.
 *
 * @author Javier Marrero
 * @param <T>
 */
public class DefaultNode<T> implements Node<T>
{

    private final Map<Node<T>, Edge> connectionsToVertex;
    private final Map<Node<T>, Edge> connectionsFromVertex;
    private T data;
    private final int label;

    /**
     * Default public constructor.
     *
     * @param label
     * @param data
     */
    public DefaultNode(int label, T data)
    {
        this.connectionsFromVertex = new LinkedHashMap<>(5);
        this.connectionsToVertex = new LinkedHashMap<>(5);
        this.data = data;
        this.label = label;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings ("unchecked")
    public boolean addEdge(Edge edge)
    {
        boolean result = (connectionsFromVertex.putIfAbsent((Node<T>) edge.getFinalNode(), edge) == null);
        if (edge.getFinalNode() instanceof DefaultNode)
        {
            DefaultNode<T> u = (DefaultNode<T>) edge.getFinalNode();
            result &= (u.connectionsToVertex.putIfAbsent(this, edge) == null);
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public T get()
    {
        return data;
    }

    @Override
    public Edge getAdjacentEdge(Node<T> v)
    {
        return getConnectionsFromVertex().get(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Edge> getConnectedEdges()
    {
        return Collections.unmodifiableSet(new CopyOnWriteArraySet<>(getConnectionsFromVertex().values()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Edge> getEdgesConnectingSelf()
    {
        return Collections.unmodifiableSet(new CopyOnWriteArraySet<>(getConnectionsToVertex().values()));
    }

    @Override
    public int getLabel()
    {
        return label;
    }

    @Override
    public boolean isAdjacent(Node<T> v)
    {
        return getConnectionsFromVertex().containsKey(v) || getConnectionsToVertex().containsKey(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(T data)
    {
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("<" + label + ":" + data.toString() + "> (");
        for (Iterator<Edge> it = getConnectionsFromVertex().values().iterator(); it.hasNext();)
        {
            Edge edge = it.next();
            builder.append(edge.getFinalNode().getLabel());

            if (edge.isWeighted())
            {
                builder.append(" <").append(edge.getWeight().toString()).append(">");
            }

            if (it.hasNext())
            {
                builder.append(", ");
            }
        }
        return builder.append(")").toString();
    }

    /**
     * @return the connectionsFromVertex
     */
    protected Map<Node<T>, Edge> getConnectionsFromVertex()
    {
        return Collections.unmodifiableMap(connectionsFromVertex);
    }

    /**
     * @return the connectionsToVertex
     */
    protected Map<Node<T>, Edge> getConnectionsToVertex()
    {
        return Collections.unmodifiableMap(connectionsToVertex);
    }
}
