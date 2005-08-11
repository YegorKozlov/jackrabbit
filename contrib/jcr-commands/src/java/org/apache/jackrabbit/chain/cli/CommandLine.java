/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.chain.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.IteratorChain;

/**
 * Command Line.
 */
public class CommandLine implements Comparable, Cloneable
{
    /** Resource bundle */
    protected ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
        .getPackage().getName()
            + ".resources");

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** Commons chain command implementation */
    private String impl;

    /** alias */
    private Collection alias = new ArrayList();

    /** Options */
    private Map options = new TreeMap();

    /** Options */
    private Map flags = new TreeMap();

    /** arguments */
    private Map arguments = new TreeMap();

    /**
     * constructor
     */
    public CommandLine()
    {
        super();
    }

    /**
     * @return required arguments
     */
    public Collection getRequiredArguments()
    {
        Predicate p = new Predicate()
        {
            public boolean evaluate(Object o)
            {
                Argument arg = (Argument) o;
                return arg.isRequired();
            }
        };
        return CollectionUtils.select(this.arguments.values(), p);
    }

    /**
     * @return required options
     */
    public Collection getRequiredOptions()
    {
        Predicate p = new Predicate()
        {
            public boolean evaluate(Object o)
            {
                Option opt = (Option) o;
                return opt.isRequired();
            }
        };
        return CollectionUtils.select(this.options.values(), p);
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return Returns the localized description.
     */
    public String getLocalizedDescription()
    {
        String str = null;
        if (description == null)
        {
            try
            {
                str = bundle.getString(this.name);
            } catch (MissingResourceException e)
            {
                str = this.name;
            }
        } else
        {
            try
            {
                str = bundle.getString(this.description);
            } catch (MissingResourceException e)
            {
                str = this.description;
            }
        }
        return str;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return Returns the flags.
     */
    public Map getFlags()
    {
        return flags;
    }

    /**
     * @param flags
     *            The flags to set.
     */
    public void setFlags(Map flags)
    {
        this.flags = flags;
    }

    /**
     * @return Returns the impl.
     */
    public String getImpl()
    {
        return impl;
    }

    /**
     * @param impl
     *            The impl to set.
     */
    public void setImpl(String impl)
    {
        this.impl = impl;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return Returns the options.
     */
    public Map getOptions()
    {
        return options;
    }

    /**
     * @param options
     *            The options to set.
     */
    public void setOptions(Map options)
    {
        this.options = options;
    }

    /**
     * @inheritDoc
     */
    public int compareTo(Object o)
    {
        CommandLine cl = (CommandLine) o;
        return name.compareTo(cl.name);
    }

    /**
     * @return all the parameters. i.e. args, options and flags.
     */
    public Iterator getAllParameters()
    {
        IteratorChain chain = new IteratorChain();
        chain.addIterator(getArguments().values().iterator());
        chain.addIterator(getOptions().values().iterator());
        chain.addIterator(getFlags().values().iterator());
        return chain;
    }

    /**
     * @return the required parameters. i.e. args, options and flags.
     */
    public Iterator getRequiredParameters()
    {
        IteratorChain chain = new IteratorChain();
        chain.addIterator(getRequiredArguments().iterator());
        chain.addIterator(getRequiredOptions().iterator());
        return chain;
    }

    /**
     * Add an argument
     * 
     * @param arg
     */
    public void addArgument(Argument arg)
    {
        if (arguments.containsKey(new Integer(arg.getPosition())))
        {
            throw new IllegalArgumentException(
                "there's an argument in the position");
        }
        this.arguments.put(new Integer(arg.getPosition()), arg);
    }

    /**
     * Add an Option
     * 
     * @param opt
     */
    public void addOption(Option opt)
    {
        this.options.put(opt.getName(), opt);
    }

    /**
     * Flag
     * 
     * @param flag
     */
    public void addFlag(Flag flag)
    {
        this.flags.put(flag.getName(), flag);
    }

    /**
     * @return Returns the arguments.
     */
    public Map getArguments()
    {
        return arguments;
    }

    /**
     * @param arguments
     *            The arguments to set.
     */
    public void setArguments(Map arguments)
    {
        this.arguments = arguments;
    }

    /**
     * @return Returns the alias.
     */
    public Collection getAlias()
    {
        return alias;
    }

    /**
     * @param alias
     *            The alias to set.
     */
    public void setAlias(Collection alias)
    {
        this.alias = alias;
    }

    /**
     * Add alias
     * 
     * @param alias
     */
    public void addAlias(String alias)
    {
        this.alias.add(alias);
    }

    /**
     * @inheritDoc
     */
    public Object clone()
    {
        CommandLine cl = new CommandLine();
        cl.alias = this.alias;
        // Arguments
        Iterator iter = this.arguments.values().iterator();
        while (iter.hasNext())
        {
            Argument arg = (Argument) iter.next();
            cl.addArgument((Argument) arg.clone());
        }
        cl.description = this.description;
        // Flags
        iter = this.flags.values().iterator();
        while (iter.hasNext())
        {
            Flag f = (Flag) iter.next();
            cl.addFlag((Flag) f.clone());
        }
        cl.impl = this.impl;
        cl.name = this.name;
        // Flags
        iter = this.options.values().iterator();
        while (iter.hasNext())
        {
            Option o = (Option) iter.next();
            cl.addOption((Option) o.clone());
        }
        return cl;
    }
}
