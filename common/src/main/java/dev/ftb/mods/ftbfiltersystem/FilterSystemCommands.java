package dev.ftb.mods.ftbfiltersystem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.DumpedFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterCache;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FilterSystemCommands {
    public static final DynamicCommandExceptionType PARSE_FAILED
            = new DynamicCommandExceptionType(object -> Component.translatable("ftbfiltersystem.message.parse_failed", object));
    public static final SimpleCommandExceptionType NOT_A_FILTER
            = new SimpleCommandExceptionType(Component.translatable("ftbfiltersystem.message.not_a_filter"));
    public static final SimpleCommandExceptionType NOT_CONFIGURED
            = new SimpleCommandExceptionType(Component.translatable("ftbfiltersystem.message.not_configured"));
    public static final SimpleCommandExceptionType NO_OFFHAND_ITEM
            = new SimpleCommandExceptionType(Component.translatable("ftbfiltersystem.message.no_offhand_item"));

    private static final Component TICK_MARK = Component.literal("✓ ").withStyle(ChatFormatting.GREEN);
    private static final Component X_MARK = Component.literal("✗ ").withStyle(ChatFormatting.RED);

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(literal(FTBFilterSystemAPI.MOD_ID)
                .then(literal("parse")
                        .then(argument("string", StringArgumentType.greedyString())
                                .executes(ctx -> tryParseCommand(ctx.getSource(), StringArgumentType.getString(ctx, "string")))
                        )
                )
                .then(literal("get_filter")
                        .executes(ctx -> tryShowFilter(ctx.getSource(), false))
                        .then(literal("pretty")
                                .executes(ctx -> tryShowFilter(ctx.getSource(), true))
                        )
                )
                .then(literal("set_filter")
                        .then(argument("string", StringArgumentType.greedyString())
                                .executes(ctx -> trySetFilter(ctx.getSource(), StringArgumentType.getString(ctx, "string")))
                        )
                )
                .then(literal("try_match")
                        .executes(ctx -> tryMatch(ctx.getSource()))
                )
                .then(literal("dump_components")
                        .requires(ctx -> ctx.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(ctx -> dumpComponents(ctx.getSource(), false))
                        .then(literal("all")
                                .executes(ctx -> dumpComponents(ctx.getSource(), true))
                        )
                )
                .then(literal("cache")
                        .requires(ctx -> ctx.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(literal("clear")
                                .executes(ctx -> clearCache(ctx.getSource()))
                        )
                )
        );
    }

    private static int tryParseCommand(CommandSourceStack source, String string) throws CommandSyntaxException {
        try {
            SmartFilter parsed = FilterParser.parse(string, source.registryAccess());
            for (DumpedFilter entry :  FTBFilterSystemAPI.api().dump(parsed)) {
                source.sendSuccess(() -> {
                            MutableComponent txt = entry.filter().getDisplayName().copy().withStyle(ChatFormatting.AQUA);
                            if (!(entry.filter() instanceof SmartFilter.Compound)) {
                                txt.append(" ").append(entry.filter().getDisplayArg(source.registryAccess()).copy().withStyle(ChatFormatting.YELLOW));
                            }
                            return Component.literal(makeDumpPrefix(entry.indent())).withStyle(ChatFormatting.YELLOW).append(txt);
                        },
                        false);
            }
        } catch (FilterException e) {
            throw PARSE_FAILED.create(e.getMessage());
        }
        return 0;
    }

    private static int tryShowFilter(CommandSourceStack source, boolean prettyPrint) throws CommandSyntaxException {
        String filterString = SmartFilterItem.getFilterString(getHeldFilter(source));
        if (filterString.isEmpty()) {
            throw NOT_CONFIGURED.create();
        }
        if (prettyPrint) {
            return tryParseCommand(source, filterString);
        } else {
            source.sendSuccess(() -> Component.literal(filterString), false);
            return 1;
        }
    }

    private static int trySetFilter(CommandSourceStack source, String string) throws CommandSyntaxException {
        try {
            SmartFilter parsed = FilterParser.parse(string, source.registryAccess());
            SmartFilterItem.setFilter(getHeldFilter(source), parsed.asString(source.registryAccess()));
            return 1;
        } catch (FilterException e) {
            source.sendFailure(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    public static int tryMatch(CommandSourceStack source) throws CommandSyntaxException {
        ItemStack offhandItem = source.getPlayerOrException().getOffhandItem();
        if (offhandItem.isEmpty()) {
            throw NO_OFFHAND_ITEM.create();
        }

        try {
            SmartFilter filter = FilterParser.parse(SmartFilterItem.getFilterString(getHeldFilter(source)), source.registryAccess());
            if (filter.test(offhandItem)) {
                source.sendSuccess(() -> TICK_MARK.copy().append(Component.translatable("ftbfiltersystem.message.matched", offhandItem.getDisplayName())), false);
                return 1;
            } else {
                source.sendSuccess(() -> X_MARK.copy().append(Component.translatable("ftbfiltersystem.message.not_matched", offhandItem.getDisplayName())), false);
                return 0;
            }
        } catch (FilterException e) {
            throw PARSE_FAILED.create(e.getMessage());
        }
    }

    private static int dumpComponents(CommandSourceStack source, boolean all) throws CommandSyntaxException {
        ItemStack stack = source.getPlayerOrException().getMainHandItem();

        if (!stack.getComponents().isEmpty()) {
            int amount = stack.getComponents().size();
            source.sendSuccess(() -> Component.translatable("ftbfiltersystem.message.components_header", amount, stack.getItem().getName(stack))
                    .withStyle(ChatFormatting.YELLOW), false);
            if (!all) {
                source.sendSuccess(() -> Component.translatable("ftbfiltersystem.message.non_default_components")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC), false);
            }
            stack.getComponents().forEach(tc -> {
                if (all || !tc.value().equals(stack.getItem().components().get(tc.type()))) {
                    source.sendSuccess(() -> Component.empty()
                                    .append(Component.literal(tc.type().toString()).withStyle(ChatFormatting.AQUA))
                                    .append(": ")
                                    .append(encodeComponent(tc)),
                            false);
                }
            });
            return 1;
        } else {
            source.sendFailure(Component.translatable("ftbfiltersystem.message.components_header_none").withStyle(ChatFormatting.GOLD));
            return 0;
        }
    }

    private static Component encodeComponent(TypedDataComponent<?> tc) {
        try {
            return Component.literal(tc.encodeValue(NbtOps.INSTANCE).getOrThrow().toString());
        } catch (IllegalStateException ignored) {
            return Component.literal("(encoding failed)").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        }
    }

    private static int clearCache(CommandSourceStack source) {
        FilterCache.INSTANCE.clear();
        source.sendSuccess(() -> Component.translatable("ftbfiltersystem.message.cache_cleared"), false);
        return 1;
    }

    // ---------------------------------------------------------------------------------------------
    // supporting methods

    private static ItemStack getHeldFilter(CommandSourceStack source) throws CommandSyntaxException {
        ItemStack stack = source.getPlayerOrException().getMainHandItem();
        if (stack.getItem() instanceof SmartFilterItem) {
            return stack;
        } else {
            throw NOT_A_FILTER.create();
        }
    }

    private static String makeDumpPrefix(int indent) {
        if (indent == 0) return "";
        String s1 = "┃ ".repeat(indent - 1);
        return s1 + "┣━";
    }
}
