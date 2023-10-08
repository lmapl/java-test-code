/*
 * AhoCorasickDoubleArrayTrie Project
 *      https://github.com/hankcs/AhoCorasickDoubleArrayTrie
 *
 * Copyright 2008-2016 hankcs <me@hankcs.com>
 * You may modify and redistribute as long as this attribution remains.
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

package source_code.ac;

import java.util.*;

/**
 * <p>
 *   字典树状态（节点）：每一个模式串中的字符都会转变为一个字典树状态
 *   一个字典树状态(节点)有如下几个功能
 * <ul>
 * <li>success; 可以成功转移到下一个状态(节点)列表，对应模式串中的下一个字符，因为前缀相同的模式串会复用节点，所以是一个list</li>
 * <li>failure; 当目标字符串在这个节点上的success没有对应下一个字符时，可以跳转到的节点列表；通常可以跳转到Root节点，或者是同字符值的其他节点</li>
 * <li>emits;  模式串的最后一个字符，所有以这个节点的字符结尾的模式串在参数Map中的顺序号</li>
 * </ul>
 * <p>
 * 根节点稍有不同，根节点没有 failure 功能，它的“failure”指的是按照字符串路径转移到下一个状态。其他节点则都有failure状态。
 * </p>
 *
 * @author Robert Bor
 */
public class State
{

    /**
     * 状态（节点）在字典树中的深度（模式串的长度）
     */
    protected final int depth;

    /**
     * fail"函数"，当目标字符串在这个节点上的success没有对应下一个字符时，通过这个"函数"，跳转到下一个节点。
     */
    private State failure = null;

    /**
     * 所有以这个节点的字符结尾的模式串在参数Map中的顺序号
     * 只要这个目标字符串可以达到这个节点，则匹配了一个模式串
     */
    private Set<Integer> emits = null;
    /**
     * 转移函数。根据字符串的下一个字符转移到下一个状态
     */
    private Map<Character, State> success = new TreeMap<Character, State>();

    /**
     * 在双数组中的对应下标（节点的唯一值）
     */
    private int index;

    /**
     * 构造深度为0的节点
     */
    public State()
    {
        this(0);
    }

    /**
     * 构造深度为depth的节点
     *
     * @param depth
     */
    public State(int depth)
    {
        this.depth = depth;
    }

    /**
     * 获取节点深度
     *
     * @return
     */
    public int getDepth()
    {
        return this.depth;
    }

    /**
     * 添加一个匹配到的模式串（这个状态对应着这个模式串)
     *
     * @param keywordIndex 以当前节点结尾的模式串在参数Map.keySet中的顺序号
     */
    public void addEmit(int keywordIndex)
    {
        if (this.emits == null)
        {
            this.emits = new TreeSet<Integer>(Collections.reverseOrder());
        }
        this.emits.add(keywordIndex);
    }

    /**
     * 获取最大的值（以当前节点结尾的模式串在参数Map.keySet中的顺序号）
     *
     * @return
     */
    public Integer getLargestValueId()
    {
        if (emits == null || emits.size() == 0) return null;

        return emits.iterator().next();
    }

    /**
     * 添加以当前节点结尾的的模式串的序号（以当前节点结尾的模式串在参数Map.keySet中的顺序号）
     *
     * @param keywordIndexs
     */
    public void addEmit(Collection<Integer> keywordIndexs)
    {
        for (int emit : keywordIndexs)
        {
            addEmit(emit);
        }
    }

    /**
     * 获取这个节点代表的模式串（们）的序号
     *
     * @return
     */
    public Collection<Integer> emit()
    {
        return this.emits == null ? Collections.<Integer>emptyList() : this.emits;
    }

    /**
     * 是否是终止状态，（是否是模式串的最后一个字符节点）
     *
     * @return
     */
    public boolean isAcceptable()
    {
        return this.depth > 0 && this.emits != null;
    }

    /**
     * 获取failure状态
     *
     * @return
     */
    public State failure()
    {
        return this.failure;
    }

    /**
     * 设置failure状态
     *
     * @param failState
     */
    public void setFailure(State failState, int fail[])
    {
        this.failure = failState;
        fail[index] = failState.index;
    }

    /**
     * 判断参数字符是否在当前节点的success map内，如果在则返回对应的节点
     *
     *
     * @param character  参数字符
     * @param ignoreRootState 是否忽略根节点，如果是根节点自己调用则应该是true，否则为false
     * @return 转移结果
     */
    private State nextState(Character character, boolean ignoreRootState)
    {
        State nextState = this.success.get(character);
        if (!ignoreRootState && nextState == null && this.depth == 0)
        {
            nextState = this;
        }
        return nextState;
    }

    /**
     * 按照character转移，根节点转移失败会返回自己（永远不会返回null）
     * 判断参数字符是否在当前节点的success map内，如果在则返回对应的节点
     * @param character
     * @return
     */
    public State nextState(Character character)
    {
        return nextState(character, false);
    }

    /**
     * 按照character转移，任何节点转移失败会返回null
     * 判断参数字符是否在当前节点的success map内，如果在则返回对应的节点
     * @param character
     * @return
     */
    public State nextStateIgnoreRootState(Character character)
    {
        return nextState(character, true);
    }

    /**
     * 增加新的节点到当前节点success的中， 即保存下一个字符范围
     * @param character
     * @return
     */
    public State addState(Character character)
    {
        State nextState = nextStateIgnoreRootState(character);
        if (nextState == null)
        {
            nextState = new State(this.depth + 1);
            this.success.put(character, nextState);
        }
        return nextState;
    }

    /**
     * 获取当前字符在字典树中的的所有下一个节点
     * @return
     */
    public Collection<State> getStates()
    {
        return this.success.values();
    }

    /**
     * 获取当前字符在字典树中的的所有下一个字符值
     * @return
     */
    public Collection<Character> getTransitions()
    {
        return this.success.keySet();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("State{");
        sb.append("depth=").append(depth);
        sb.append(", ID=").append(index);
        sb.append(", emits=").append(emits);
        sb.append(", success=").append(success.keySet());
        sb.append(", failureID=").append(failure == null ? "-1" : failure.index);
        sb.append(", failure=").append(failure);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 获取success表
     * @return
     */
    public Map<Character, State> getSuccess()
    {
        return success;
    }

    /**
     * 获取节点ID
     * @return
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * 设置节点ID
     * @param index
     */
    public void setIndex(int index)
    {
        this.index = index;
    }
}
