package myau.command.commands;

import myau.Myau;
import myau.command.Command;
import myau.util.ChatUtil;
import myau.util.DomainUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class ReplaceIpCommand extends Command {
    public ReplaceIpCommand() {
        super(new ArrayList<String>(Arrays.asList("replaceip", "rip")));
    }

    @Override
    public void runCommand(ArrayList<String> args) {
        if (args.isEmpty()) {
            if (DomainUtil.isEnabled()) {
                ChatUtil.sendFormatted(String.format(
                        "%sCurrent replacement: &o%s&r",
                        Myau.clientName,
                        DomainUtil.getReplacementDomain()
                ));
            } else {
                ChatUtil.sendFormatted(String.format(
                        "%sUsage: .replaceip <domain> or .replaceip off&r",
                        Myau.clientName
                ));
            }
            return;
        }

        String arg = args.get(0).toLowerCase();
        if (arg.equals("off") || arg.equals("disable") || arg.equals("reset")) {
            DomainUtil.setReplacementDomain("");
            ChatUtil.sendFormatted(String.format("%sDomain replacement &cdisabled&r", Myau.clientName));
        } else {
            String domain = String.join(" ", args);
            DomainUtil.setReplacementDomain(domain);
            ChatUtil.sendFormatted(String.format(
                    "%sDomain will be replaced with: &a%s&r",
                    Myau.clientName,
                    domain
            ));
        }
    }
}
