"use client";

import {
  createContext,
  useContext,
  useMemo,
  useState,
  type ComponentProps,
  type Dispatch,
  type ReactNode,
  type SetStateAction,
} from "react";
import Link from "next/link";
import { Menu, X } from "lucide-react";
import { AnimatePresence, motion } from "motion/react";

import { cn } from "@/lib/utils";

type SidebarContextValue = {
  open: boolean;
  setOpen: Dispatch<SetStateAction<boolean>>;
  animate: boolean;
};

const SidebarContext = createContext<SidebarContextValue | undefined>(
  undefined,
);

export function useSidebar() {
  const ctx = useContext(SidebarContext);
  if (!ctx) {
    throw new Error("useSidebar must be used inside a Sidebar provider");
  }
  return ctx;
}

type SidebarProviderProps = {
  children: ReactNode;
  open?: boolean;
  setOpen?: Dispatch<SetStateAction<boolean>>;
  animate?: boolean;
};

export function SidebarProvider({
  children,
  open: openProp,
  setOpen: setOpenProp,
  animate = true,
}: SidebarProviderProps) {
  const [openState, setOpenState] = useState(false);
  const open = openProp ?? openState;
  const setOpen = setOpenProp ?? setOpenState;
  const value = useMemo(() => ({ open, setOpen, animate }), [open, setOpen, animate]);
  return (
    <SidebarContext.Provider value={value}>{children}</SidebarContext.Provider>
  );
}

export function Sidebar({
  children,
  open,
  setOpen,
  animate,
}: SidebarProviderProps) {
  return (
    <SidebarProvider open={open} setOpen={setOpen} animate={animate}>
      {children}
    </SidebarProvider>
  );
}

type SidebarBodyProps = ComponentProps<typeof motion.div>;

export function SidebarBody(props: SidebarBodyProps) {
  return (
    <>
      <DesktopSidebar {...props} />
      <MobileSidebar {...(props as ComponentProps<"div">)} />
    </>
  );
}

export function DesktopSidebar({
  className,
  children,
  ...props
}: SidebarBodyProps) {
  const { open, setOpen, animate } = useSidebar();
  return (
    <motion.div
      className={cn(
        "bg-palco text-marquee/80 border-r border-palco-surface hidden h-screen w-[72px] shrink-0 flex-col px-3 py-4 md:flex",
        className,
      )}
      animate={{ width: animate ? (open ? 264 : 72) : 264 }}
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
      {...props}
    >
      {children}
    </motion.div>
  );
}

export function MobileSidebar({
  className,
  children,
  ...props
}: ComponentProps<"div">) {
  const { open, setOpen } = useSidebar();
  return (
    <>
      <div
        className={cn(
          "bg-palco text-marquee/80 border-palco-surface flex h-14 w-full items-center justify-between border-b px-4 md:hidden",
        )}
        {...props}
      >
        <button
          type="button"
          onClick={() => setOpen(true)}
          aria-label="Abrir menu"
          className="text-marquee/80 hover:text-marquee p-1"
        >
          <Menu className="h-5 w-5" />
        </button>
      </div>
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ x: "-100%", opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: "-100%", opacity: 0 }}
            transition={{ duration: 0.25, ease: "easeInOut" }}
            className={cn(
              "bg-palco text-marquee/80 fixed inset-0 z-50 flex h-full w-full flex-col p-6 md:hidden",
              className,
            )}
          >
            <button
              type="button"
              className="text-marquee/80 hover:text-marquee absolute right-4 top-4 z-40"
              onClick={() => setOpen(false)}
              aria-label="Fechar menu"
            >
              <X className="h-5 w-5" />
            </button>
            {children}
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}

export type SidebarLinkDef = {
  label: string;
  href: string;
  icon: ReactNode;
};

export function SidebarLink({
  link,
  className,
  active,
  ...props
}: {
  link: SidebarLinkDef;
  className?: string;
  active?: boolean;
} & Omit<ComponentProps<typeof Link>, "href">) {
  const { open, animate } = useSidebar();
  return (
    <Link
      href={link.href}
      className={cn(
        "group/sidebar flex items-center justify-start gap-3 rounded-lg px-3 py-2 text-sm transition-colors",
        active
          ? "bg-vinho text-marquee shadow-sm"
          : "text-marquee/75 hover:bg-palco-surface hover:text-marquee",
        className,
      )}
      title={!open ? link.label : undefined}
      {...props}
    >
      <span className="flex h-5 w-5 shrink-0 items-center justify-center">
        {link.icon}
      </span>
      <motion.span
        animate={{
          display: animate ? (open ? "inline-block" : "none") : "inline-block",
          opacity: animate ? (open ? 1 : 0) : 1,
        }}
        className="!m-0 inline-block !p-0 whitespace-pre transition duration-150 group-hover/sidebar:translate-x-1"
      >
        {link.label}
      </motion.span>
    </Link>
  );
}
