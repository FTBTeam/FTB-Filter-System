package dev.ftb.mods.ftbfiltersystem.util;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.IntStream;

public class NBTUtil {
    /**
     * Quite like the vanilla {@link net.minecraft.nbt.NbtUtils#compareNbt(Tag, Tag, boolean)} but also has the option
     * for fuzzy checking. Order of the tags matters! Supply the tag being checked against first (e.g. the filter),
     * and the tag being checked (e.g. the actual item's NBT) second.
     *
     * @param tagA tag to compare against
     * @param tagB tag being tested
     * @param fuzzy if true, fuzzy match for compound tags: fields in compoundTagB but not in compoundTagA don't cause a
     *                match failure
     * @param compareLists if true, recursively deep-compare lists (otherwise simple .equals() check)
     * @return true if there's a match, false otherwise
     */
    public static boolean compareNbt(@Nullable Tag tagA, @Nullable Tag tagB, boolean fuzzy, boolean compareLists) {
        if (tagA == tagB) {
            return true;
        } else if (tagA == null) {
            return true;
        } else if (tagB == null) {
            return false;
        } else if (!tagA.getType().equals(tagB.getType())) {
            return false;
        } else if (tagA instanceof CompoundTag compoundA && tagB instanceof CompoundTag compoundB) {
            Set<String> keysA = compoundA.getAllKeys();
            Set<String> keysB = compoundB.getAllKeys();
            if (!fuzzy) {
                if (keysA.size() != keysB.size() || Sets.intersection(keysA, keysB).size() != keysA.size()) {
                    return false;
                }
            }
            return keysA.stream().allMatch(key -> compareNbt(compoundA.get(key), compoundB.get(key), fuzzy, compareLists));
        } else if (compareLists && tagA instanceof ListTag listA && tagB instanceof ListTag listB) {
            if (listA.isEmpty()) {
                return listB.isEmpty();
            } else if (listA.size() != listB.size()) {
                return false;
            } else {
                return IntStream.range(0, listA.size()).allMatch(i -> compareNbt(listA.get(i), listB.get(i), fuzzy, true));
            }
        } else {
            return tagA.equals(tagB);
        }
    }
}
